package com.projects.resolver.service;

import com.projects.resolver.dto.Subscription.CheckoutRequest;
import com.projects.resolver.dto.Subscription.CheckoutResponse;
import com.projects.resolver.dto.Subscription.PortalResponse;
import com.stripe.model.StripeObject;

import java.util.Map;

public interface PaymentProcessor {

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest checkoutRequest);

    PortalResponse openCustoemrPortal();

    void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metaData);
}
