package com.projects.resolver.mapper;

import com.projects.resolver.dto.Subscription.PlanResponse;
import com.projects.resolver.dto.Subscription.SubscriptionResponse;
import com.projects.resolver.entity.Plan;
import com.projects.resolver.entity.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    PlanResponse toPlanResponse(Plan plan);
}
