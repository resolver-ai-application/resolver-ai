package com.projects.resolver.service.Impl;

import com.projects.resolver.dto.Subscription.PlanResponse;
import com.projects.resolver.dto.Subscription.SubscriptionResponse;
import com.projects.resolver.entity.UsageLog;
import com.projects.resolver.repositories.UsageLogRepository;
import com.projects.resolver.security.AuthUtil;
import com.projects.resolver.service.SubscriptionService;
import com.projects.resolver.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {

    private final UsageLogRepository usageLogRepository;
    private final AuthUtil authUtil;
    private final SubscriptionService subscriptionService;

    // record token usage everyday
    @Override
    public void recordTokenUsage(Long userId, int actualTokens) {
        LocalDate today = LocalDate.now();
        UsageLog todayLog = usageLogRepository.findUserIdAndDate(userId,today)
                .orElseGet(()->createNewDailyLog(userId,today));
        todayLog.setTokensUsed(actualTokens + todayLog.getTokensUsed());
        usageLogRepository.save(todayLog);
    }

    @Override
    public void checkDailyTokensUsage() {
        Long userId = authUtil.getCurrentUserId();
        SubscriptionResponse subscriptionResponse = subscriptionService.getCurrentSubscription();
        PlanResponse plan = subscriptionResponse.plan();

        if(plan==null) return;

        LocalDate today = LocalDate.now();
        UsageLog todayLog = usageLogRepository.findUserIdAndDate(userId,today).orElseGet(()->createNewDailyLog(userId,today));

        if(plan.unlimitedAI()) return;

        int currentUsage = todayLog.getTokensUsed();
        int limit = plan.maxTokensPerDay();

        if(currentUsage>=limit){
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Daily limit reached, Upgrade now");
        }
    }

    private UsageLog createNewDailyLog(Long userId, LocalDate date){
        UsageLog newLog = UsageLog.builder().userId(userId).date(date).tokensUsed(0).build();
        return usageLogRepository.save(newLog);
    }
}
