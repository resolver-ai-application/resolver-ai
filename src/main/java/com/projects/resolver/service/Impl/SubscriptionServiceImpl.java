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
import com.projects.resolver.repositories.ProjectMemberRepository;
import com.projects.resolver.repositories.SubscriptionRepository;
import com.projects.resolver.repositories.UserRepository;
import com.projects.resolver.security.AuthUtil;
import com.projects.resolver.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionServiceImpl implements SubscriptionService {

    AuthUtil authUtil;
    SubscriptionRepository subscriptionRepository;
    SubscriptionMapper subscriptionMapper;
    UserRepository userRepository;
    PlanRepository planRepository;
    ProjectMemberRepository projectMemberRepository;
    Integer FREE_TIER_PROJECTS_ALLOWED=1;


    /**
     * Fetch information about current subscription
     * @return
     */
    @Override
    public SubscriptionResponse getCurrentSubscription() {
        log.info("Current Subscription Response");
        Long userId = authUtil.getCurrentUserId();
        var currentSubscription =  subscriptionRepository.findByUserIdAndStatusIn(userId, Set.of(
                SubscriptionStatus.ACTIVE, SubscriptionStatus.PAST_DUE,
                SubscriptionStatus.TRAILING
        )).orElse(new Subscription());
        return subscriptionMapper.toSubscriptionResponse(currentSubscription);
    }

    /**
     * To activate a subscription[Fresh]
     * @param userId
     * @param planId
     * @param subscriptionId
     * @param customerId
     */
    @Override
    public void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId) {
        log.info("Active subscription for User: {}, chosen plan:{}, subscription id: {}",
                userId, planId, subscriptionId);
        boolean exists = subscriptionRepository.existsByStripeSubscriptionId(subscriptionId);
        if(exists) return;
        User user = getUser(userId);
        Plan plan = getPlan(planId);

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .stripeSubscriptionId(subscriptionId)
                .status(SubscriptionStatus.INCOMPLETE).build();
        log.info("Subscription status: {}",subscription.getStatus());
        subscriptionRepository.save(subscription);
    }

    /**
     * Update Subscription Plan from P1 to P2
     * @param subscriptionId
     * @param status
     * @param periodStart
     * @param periodEnd
     * @param cancelAtPeriodEnd
     * @param planId
     */
    @Override
    @Transactional
    public void updateSubscription(String subscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {
        Subscription subscription = getSubscription(subscriptionId);
        boolean hasSubscriptionUpdated = false;
        if(status!=null && status!=subscription.getStatus()){
            subscription.setStatus(status);
            hasSubscriptionUpdated=true;
        }
        if(periodStart!=null && !periodStart.equals(subscription.getCurrentPeriodStart())){
            subscription.setCurrentPeriodStart(periodStart);
            hasSubscriptionUpdated=true;
        }
        if(periodEnd!=null && !periodEnd.equals(subscription.getCurrentPeriodEnd())){
            subscription.setCurrentPeriodEnd(periodEnd);
            hasSubscriptionUpdated=true;
        }
        if(cancelAtPeriodEnd!=null && !cancelAtPeriodEnd.equals(subscription.getCancelAtPeriodEnd())){
            subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);
            hasSubscriptionUpdated=true;
        }
        if(planId!=null && !planId.equals(subscription.getPlan().getId())){
            Plan newPlan = getPlan(planId);
            subscription.setPlan(newPlan);
            hasSubscriptionUpdated=true;
        }
        log.info("Subscription status: {}",subscription.getStatus());
        if(hasSubscriptionUpdated){
            log.debug("Subscripiton has been updated: {}",subscriptionId);
            subscriptionRepository.save(subscription);
        }
    }

    /**
     * To cancel Subscription
     * @param subscriptionId
     */
    @Override
    public void cancelSubscription(String subscriptionId) {
        log.info("Cancel subscription {} ", subscriptionId);
        Subscription subscription = getSubscription(subscriptionId);
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        log.info("Subscription status: {}",subscription.getStatus());
        subscriptionRepository.save(subscription);
        //notify to user, email, ...
    }

    /**
     * To renew Subscription of active subscription, Inoive paid
     * @param subscriptionId
     * @param periodStart
     * @param periodEnd
     */
    @Override
    public void renewSubscriptionPeriod(String subscriptionId, Instant periodStart, Instant periodEnd) {
        log.info("Renew Subscription : {}, {}, {}",subscriptionId, periodStart, periodEnd);
        Subscription subscription = getSubscription(subscriptionId);
        Instant newStart = periodStart!=null ? periodStart : subscription.getCurrentPeriodEnd();
        subscription.setCurrentPeriodStart(newStart);
        subscription.setCurrentPeriodEnd(periodEnd);
        if(subscription.getStatus()==SubscriptionStatus.PAST_DUE || subscription.getStatus()==SubscriptionStatus.ACTIVE){
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }
        log.info("Subscription status: {}",subscription.getStatus());
        subscriptionRepository.save(subscription);
    }

    /**
     * Subscription Marked as Past Due once subscripiton duration over
     * @param subscriptionId
     */
    @Override
    public void markSubscriptionPastDue(String subscriptionId) {
        log.info("Subscription id: {} is past due now",subscriptionId);
        Subscription subscription = getSubscription(subscriptionId);
        if(subscription.getStatus()==SubscriptionStatus.PAST_DUE){
            log.info("Subscription is already due for subscriptionId: {}",subscriptionId);
            return;
        }
        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        log.info("Subscription status: {}",subscription.getStatus());
        subscriptionRepository.save(subscription);
        //notify to user, email, ...
    }



    @Override
    public boolean canCreateNewProject() {
        Long userId = authUtil.getCurrentUserId();
        SubscriptionResponse currentSubscription = getCurrentSubscription();
        int noOfProjects = projectMemberRepository.countProjectOwnedByUser(userId);
        if(currentSubscription.plan()==null){
            return noOfProjects<FREE_TIER_PROJECTS_ALLOWED;
        }
        return noOfProjects<currentSubscription.plan().maxProjects();
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
