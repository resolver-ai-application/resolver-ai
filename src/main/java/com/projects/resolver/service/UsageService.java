package com.projects.resolver.service;

import com.projects.resolver.dto.Subscription.PlanLimitsResponse;
import com.projects.resolver.dto.Subscription.UsageTodayResponse;
import org.jspecify.annotations.Nullable;

public interface UsageService {

    void recordTokenUsage(Long userId, int actualTokens);

    void checkDailyTokensUsage();
}
