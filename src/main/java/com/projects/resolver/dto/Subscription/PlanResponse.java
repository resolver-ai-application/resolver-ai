package com.projects.resolver.dto.Subscription;

public record PlanResponse(
        Long id,
        String name,
        Integer maxProjects,
        Integer maxTokensPerDay,
        Boolean unlimitedAI,
        String price
) {
}
