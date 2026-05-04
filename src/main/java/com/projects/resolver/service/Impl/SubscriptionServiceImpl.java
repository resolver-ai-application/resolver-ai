package com.projects.resolver.service.Impl;

import com.projects.resolver.dto.Subscription.CheckoutRequest;
import com.projects.resolver.dto.Subscription.CheckoutResponse;
import com.projects.resolver.dto.Subscription.PortalResponse;
import com.projects.resolver.dto.Subscription.SubscriptionResponse;
import com.projects.resolver.security.AuthUtil;
import com.projects.resolver.service.SubscriptionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionServiceImpl implements SubscriptionService {


    @Override
    public SubscriptionResponse getCurrentSubscription(Long userId) {
        return null;
    }

}
