package com.projects.resolver.dto.Subscription;

public record PlanLimitsResponse(
        String planName,
        Integer maxProjects,
        Integer maxTokensPerDay,
        boolean unlimitedAi
) {
}
