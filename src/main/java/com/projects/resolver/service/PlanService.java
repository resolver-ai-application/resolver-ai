package com.projects.resolver.service;

import com.projects.resolver.dto.Subscription.PlanResponse;

import java.util.List;

public interface PlanService {

    List<PlanResponse> getAllActivePlans();
}
