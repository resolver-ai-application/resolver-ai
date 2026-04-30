package com.projects.resolver.service.Impl;

import com.projects.resolver.dto.Subscription.PlanLimitsResponse;
import com.projects.resolver.dto.Subscription.UsageTodayResponse;
import com.projects.resolver.service.UsageService;
import org.springframework.stereotype.Service;

@Service
public class UsageServiceImpl implements UsageService {
    @Override
    public UsageTodayResponse getTodayUsageOfUser(Long userId) {
        return null;
    }

    @Override
    public PlanLimitsResponse getCurrentSubscriptionLimitsOfUser(Long userId) {
        return null;
    }
}
