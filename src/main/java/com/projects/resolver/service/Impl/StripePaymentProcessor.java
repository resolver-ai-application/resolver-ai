package com.projects.resolver.service.Impl;

import com.projects.resolver.dto.Subscription.CheckoutRequest;
import com.projects.resolver.dto.Subscription.CheckoutResponse;
import com.projects.resolver.dto.Subscription.PortalResponse;
import com.projects.resolver.entity.Plan;
import com.projects.resolver.entity.User;
import com.projects.resolver.enums.SubscriptionStatus;
import com.projects.resolver.exceptions.BadRequestException;
import com.projects.resolver.exceptions.ResourceNotFoundException;
import com.projects.resolver.repositories.PlanRepository;
import com.projects.resolver.repositories.UserRepository;
import com.projects.resolver.security.AuthUtil;
import com.projects.resolver.service.PaymentProcessor;

import com.projects.resolver.service.SubscriptionService;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentProcessor implements PaymentProcessor {

    private final AuthUtil authUtil;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;

    @Value("${client.url}")
    private String frontendUrl;

    /**
     * Create a stripe url and send back to front-end User, User will redirect to stripe page
     * @param checkoutRequest
     * @return
     */
    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest checkoutRequest) {
        Plan plan =
                planRepository
                        .findById(checkoutRequest.planId())
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "plan not found", checkoutRequest.planId().toString()));
        Long userId = authUtil.getCurrentUserId();
        User user = getUser(userId);

        SessionCreateParams.Builder builder =
                SessionCreateParams.builder()
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(plan.getStripePriceId())
                                        .setQuantity(1L)
                                        .build())
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setSubscriptionData(
                                SessionCreateParams.SubscriptionData.builder()
                                        .setBillingMode(
                                                SessionCreateParams.SubscriptionData.BillingMode.builder()
                                                        .setType(SessionCreateParams.SubscriptionData.BillingMode.Type.FLEXIBLE)
                                                        .build())
                                        .build())
                        .setSuccessUrl(frontendUrl + "/success.html?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(frontendUrl + "/cancel.html")
                        .putMetadata("user_id", userId.toString())
                        .putMetadata("plan_id", plan.getId().toString());

        try {
            String stripeCustomerId = user.getStripeCustomerId();
            if (Objects.isNull(stripeCustomerId) || stripeCustomerId.isEmpty())
                builder.setCustomerEmail(user.getUsername());
            else builder.setCustomer(stripeCustomerId);
            SessionCreateParams params = builder.build();
            Session session = Session.create(params);//stripe backend
            return new CheckoutResponse(session.getUrl());
        } catch (StripeException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public PortalResponse openCustomerPortal() {
        Long userId = authUtil.getCurrentUserId();
        User user = getUser(userId);
        String stripeCustomerId = user.getStripeCustomerId();

        if(stripeCustomerId==null || stripeCustomerId.isEmpty()){
            throw new BadRequestException("User does not have a stripe customer id, userId:" + userId);
        }

        try {
            var portalSession = com.stripe.model.billingportal.Session.create(
                    com.stripe.param.billingportal.SessionCreateParams.builder()
                            .setCustomer(stripeCustomerId)
                            .setReturnUrl(frontendUrl)
                            .build()
            );
            return new PortalResponse(portalSession.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metaData) {
        log.info("Event from webhook: {}, {}, {}",type, stripeObject, metaData);
        switch(type){
            case "checkout.session.completed" -> handleCheckoutSessionCompleted((Session) stripeObject, metaData);//on-time, on checkout completed
            case "customer.subscription.updated" -> handleCheckoutSubscriptionUpdated((Subscription) stripeObject);//when user cancels, upgrades, any update
            case "customer.subscription.deleted" -> handleCheckoutSubscriptionDeleted((Subscription) stripeObject);//when subs ends
            case "invoice.paid" -> handleInvoicePaid((Invoice) stripeObject);//when invoice is paid
            case "invoice.payment_failed" -> handleInvoicePaymentFailed((Invoice) stripeObject);//when invoice is not paid, mark as past_due
            default -> log.debug("Ignoring the event: {}",type);
        }
    }

    private void handleCheckoutSessionCompleted(Session session, Map<String, String> metaData){
        if(session==null){
            log.error("session object is null, handleCheckoutSessionCompleted");
            return;
        }
        Long userId = Long.parseLong(metaData.get("user_id"));
        Long planId = Long.parseLong(metaData.get("plan_id"));
        String subscriptionId = session.getSubscription();
        String customerId = session.getCustomer();
        User user = getUser(userId);
        if(user.getStripeCustomerId()==null){
            user.setStripeCustomerId(customerId);
            userRepository.save(user);
        }
        subscriptionService.activateSubscription(userId, planId, subscriptionId, customerId);
    }

    private void handleCheckoutSubscriptionUpdated(Subscription subscription){
        if(Objects.isNull(subscription)){
            log.error("subscription object is null, handleCheckoutSubscriptionUpdated");
            return;
        }
        SubscriptionStatus status = mapStripeStatusToEnum(subscription.getStatus());
        if(Objects.isNull(status)){
            log.warn("Unknown status '{}' for subscription '{}'", subscription.getStatus(), subscription.getId());
            return;
        }
        SubscriptionItem item = subscription.getItems().getData().get(0);
        Instant periodStart = toInstant(item.getCurrentPeriodStart());
        Instant periodEnd = toInstant(item.getCurrentPeriodEnd());
        Long planId = resovlePlanId(item.getPrice());
        subscriptionService.updateSubscription(subscription.getId(), status, periodStart, periodEnd, subscription.getCancelAtPeriodEnd(),planId);
    }

    private void handleCheckoutSubscriptionDeleted(Subscription subscription){
        if(Objects.isNull(subscription)){
            log.error("subscription object is null, handleCheckoutSubscriptionDeleted");
            return;
        }
        subscriptionService.cancelSubscription(subscription.getId());
    }

    private void handleInvoicePaid(Invoice invoice){
        String subscriptionId = extractSubscriptionId(invoice);
        if(subscriptionId ==null){
            log.error("subscription object is null, handleInvoicePaid");
            return ;
        }
        try {
            Subscription subscription = Subscription.retrieve(subscriptionId);//sdk calling strip information
            var item = subscription.getItems().getData().get(0);
            Instant periodStart = toInstant(item.getCurrentPeriodStart());
            Instant periodEnd = toInstant(item.getCurrentPeriodEnd());
            subscriptionService.renewSubscriptionPeriod(subscriptionId, periodStart, periodEnd);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleInvoicePaymentFailed(Invoice invoice){
        String subscriptionId = extractSubscriptionId(invoice);
        if(subscriptionId ==null){
            log.error("subscription object is null, handleInvoicePaymentFailed");
            return ;
        }
        subscriptionService.markSubscriptionPastDue(subscriptionId);
    }

    //////// Utility Methods

    private User getUser(Long userId){
        return userRepository.findById(userId).orElseThrow(
                ()-> new ResourceNotFoundException("user",userId.toString())
        );
    }

    private SubscriptionStatus mapStripeStatusToEnum(String status) {
        return switch (status) {
            case "active" -> SubscriptionStatus.ACTIVE;
            case "trailing" -> SubscriptionStatus.TRAILING;
            case "past_due", "unpaid", "paused", "incomplete_expired" -> SubscriptionStatus.PAST_DUE;
            case "canceled" -> SubscriptionStatus.CANCELLED;
            case "incomplete" -> SubscriptionStatus.INCOMPLETE;
            default -> {
                log.warn("Unmapped Stripe status: {}", status);
                yield null;
            }
        };
    }

    private Instant toInstant(Long epoch){
        return epoch!=null? Instant.ofEpochSecond(epoch):null;
    }

    private Long resovlePlanId(Price price){
        if(price==null || price.getId()==null) return null;
        return planRepository.findByStripePriceId(price.getId()).map(Plan::getId).orElse(null);
    }

    private String extractSubscriptionId(Invoice invoice){
        var parent = invoice.getParent();
        if(parent==null) return null;
        var subDetails = parent.getSubscriptionDetails();
        if(subDetails==null) return null;
        return subDetails.getSubscription();
    }
}
