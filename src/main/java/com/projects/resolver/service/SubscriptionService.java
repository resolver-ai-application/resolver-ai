package com.projects.resolver.service;

import com.projects.resolver.dto.Subscription.CheckoutRequest;
import com.projects.resolver.dto.Subscription.CheckoutResponse;
import com.projects.resolver.dto.Subscription.PortalResponse;
import com.projects.resolver.dto.Subscription.SubscriptionResponse;
import org.jspecify.annotations.Nullable;

public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription(Long userId);

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest checkoutRequest, Long userId);

    PortalResponse openCustoemrPortal(Long userId);
}
