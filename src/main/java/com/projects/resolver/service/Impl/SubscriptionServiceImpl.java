package com.projects.resolver.service.Impl;

import com.projects.resolver.dto.Subscription.CheckoutRequest;
import com.projects.resolver.dto.Subscription.CheckoutResponse;
import com.projects.resolver.dto.Subscription.PortalResponse;
import com.projects.resolver.dto.Subscription.SubscriptionResponse;
import com.projects.resolver.entity.Plan;
import com.projects.resolver.entity.Subscription;
import com.projects.resolver.entity.User;
import com.projects.resolver.enums.SubscriptionStatus;
import com.projects.resolver.exceptions.ResourceNotFoundException;
import com.projects.resolver.mapper.SubscriptionMapper;
import com.projects.resolver.repositories.PlanRepository;
import com.projects.resolver.repositories.SubscriptionRepository;
import com.projects.resolver.repositories.UserRepository;
import com.projects.resolver.security.AuthUtil;
import com.projects.resolver.service.SubscriptionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionServiceImpl implements SubscriptionService {

    AuthUtil authUtil;
    SubscriptionRepository subscriptionRepository;
    SubscriptionMapper subscriptionMapper;
    UserRepository userRepository;
    PlanRepository planRepository;


    @Override
    public SubscriptionResponse getCurrentSubscription() {
        Long userId = authUtil.getCurrentUserId();
        var currentSubscription =  subscriptionRepository.findByUserIdAndStatusIn(userId, Set.of(
                SubscriptionStatus.ACTIVE, SubscriptionStatus.PAST_DUE,
                SubscriptionStatus.TRAILING
        )).orElse(new Subscription());
        return subscriptionMapper.toSubscriptionResponse(currentSubscription);
    }

    @Override
    public void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId) {
        boolean exists = subscriptionRepository.existsByStripeSubscriptionId(subscriptionId);
        if(exists) return;
        User user = getUser(userId);
        Plan plan = getPlan(planId);

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .stripeSubscriptionId(subscriptionId)
                .status(SubscriptionStatus.INCOMPLETE).build();

        subscriptionRepository.save(subscription);
    }

    @Override
    public void updateSubscription(String subscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {

    }

    @Override
    public void cancelSubscription(String subscriptionId) {

    }

    @Override
    public void renewSubscriptionPeriod(String subscriptionId, Instant periodStart, Instant periodEnd) {
        Subscription subscription = getSubscription(subscriptionId);
        Instant newStart = periodStart!=null ? periodStart : subscription.getCurrentPeriodEnd();
        subscription.setCurrentPeriodStart(newStart);
        subscription.setCurrentPeriodEnd(periodEnd);
        if(subscription.getStatus()==SubscriptionStatus.PAST_DUE || subscription.getStatus()==SubscriptionStatus.ACTIVE){
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }
        subscriptionRepository.save(subscription);
    }

    @Override
    public void markSubscriptionPastDue(String subscriptionId) {

    }

    ///// Utility
    private Plan getPlan(Long planId) {
        return planRepository.findById(planId).orElseThrow(
                () -> new ResourceNotFoundException("Plan", planId.toString())
        );
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                ()-> new ResourceNotFoundException("User",userId.toString())
        );
    }

    private Subscription getSubscription(String subscriptionId){
        return subscriptionRepository.findByStripeSubscriptionId(subscriptionId).orElseThrow(
                ()-> new ResourceNotFoundException("Subscription", subscriptionId)
        );
    }

}
