package com.projects.resolver.controller;

import com.projects.resolver.dto.Subscription.*;
import com.projects.resolver.service.PaymentProcessor;
import com.projects.resolver.service.PlanService;
import com.projects.resolver.service.SubscriptionService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BillingController {

    private final PlanService planService;
    private final SubscriptionService subscriptionService;
    private final PaymentProcessor paymentProcessor;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @GetMapping("/api/plans")
    public ResponseEntity<List<PlanResponse>> getAllPlans(){
        return ResponseEntity.ok(planService.getAllActivePlans());
    }

    @GetMapping("/api/me/subscription")
    public ResponseEntity<SubscriptionResponse> getMySubscription(){
        Long userId = 1L;
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription(userId));
    }

    @PostMapping("/api/payments/checkout")
    public ResponseEntity<CheckoutResponse> createCheckoutResponse(@RequestBody CheckoutRequest checkoutRequest){
        return  ResponseEntity.ok(paymentProcessor.createCheckoutSessionUrl(checkoutRequest));
    }

    @PostMapping("/api/payments/portal")
    public ResponseEntity<PortalResponse> openCustomerPortal(){
        return  ResponseEntity.ok(paymentProcessor.openCustoemrPortal());
    }

    @PostMapping("/webhooks/payment")
    public ResponseEntity<String> handlePaymentWebhook
            (@RequestBody String payload,
             @RequestHeader("Stripe-Signature") String signature
            ){
        try {
            Event event = Webhook.constructEvent(payload, signature, webhookSecret);
            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;
            if(deserializer.getObject().isPresent())
                stripeObject = deserializer.getObject().get();
            else{
                try{
                    stripeObject = deserializer.deserializeUnsafe();
                    if(stripeObject == null){
                        return ResponseEntity.ok().build();
                    }
                } catch (Exception e){
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deserialization failed");
                }
            }
            Map<String, String> metaData = new HashMap<>();
            if(stripeObject instanceof Session session)
                metaData = session.getMetadata();
            paymentProcessor.handleWebhookEvent(event.getType(), stripeObject, metaData);
            return ResponseEntity.ok().build();
        } catch (SignatureVerificationException ex){
            throw new RuntimeException(ex);
        }

    }
}
