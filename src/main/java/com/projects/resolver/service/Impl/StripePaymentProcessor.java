package com.projects.resolver.service.Impl;

import com.projects.resolver.dto.Subscription.CheckoutRequest;
import com.projects.resolver.dto.Subscription.CheckoutResponse;
import com.projects.resolver.dto.Subscription.PortalResponse;
import com.projects.resolver.entity.Plan;
import com.projects.resolver.entity.User;
import com.projects.resolver.exceptions.ResourceNotFoundException;
import com.projects.resolver.repositories.PlanRepository;
import com.projects.resolver.repositories.UserRepository;
import com.projects.resolver.security.AuthUtil;
import com.projects.resolver.service.PaymentProcessor;

import com.stripe.exception.StripeException;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StripePaymentProcessor implements PaymentProcessor {

    private final AuthUtil authUtil;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    @Value("${client.url}")
    private String frontendUrl;

    /**
     * Create a stripe url and send back to front-end User, User will redirect to stripe page
     * @param checkoutRequest
     * @return
     */
    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest checkoutRequest) {
        Plan plan = planRepository.findById(checkoutRequest.planId()).orElseThrow(
                ()-> new ResourceNotFoundException("Plan",checkoutRequest.planId().toString())
        );
        Long userId = authUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(
                ()->new ResourceNotFoundException("User",userId.toString())
        );
        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPrice(plan.getStripPriceId()).setQuantity(1L).build())
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSubscriptionData(
                        SessionCreateParams.SubscriptionData.builder()
                                .setBillingMode(SessionCreateParams.SubscriptionData.BillingMode.builder()
                                        .setType(SessionCreateParams.SubscriptionData.BillingMode.Type.FLEXIBLE)
                                        .build())
                                .build()
                )
                .setSuccessUrl(frontendUrl + "/success.html?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/cancel.html")
                .putMetadata("user_id",userId.toString())
                .putMetadata("plan_id",plan.getId().toString());
        try {
            String stripeId = user.getStripeCustomerId();
            if(Objects.isNull(stripeId) || Strings.isBlank(stripeId)){
                builder.setCustomer(stripeId);
            } else {
                builder.setCustomer(stripeId);
            }
            SessionCreateParams params = builder.build();
            Session session = Session.create(params);
            return new CheckoutResponse(session.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PortalResponse openCustoemrPortal() {
        return null;
    }

    @Override
    public void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metaData) {

    }
}
