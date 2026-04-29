package com.projects.resolver.service;

import com.projects.resolver.dto.Subscription.PlanLimitsResponse;
import com.projects.resolver.dto.Subscription.UsageTodayResponse;
import org.jspecify.annotations.Nullable;

public interface UsageService {
    UsageTodayResponse getTodayUsageOfUser(Long userId);

    PlanLimitsResponse getCurrentSubscriptionLimitsOfUser(Long userId);
}
