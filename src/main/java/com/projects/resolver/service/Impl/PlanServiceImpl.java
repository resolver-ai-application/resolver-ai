package com.projects.resolver.service.Impl;

import com.projects.resolver.dto.Subscription.PlanResponse;
import com.projects.resolver.service.PlanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {
    @Override
    public List<PlanResponse> getAllActivePlans() {
        return List.of();
    }
}
