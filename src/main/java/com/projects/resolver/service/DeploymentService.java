package com.projects.resolver.service;

import com.projects.resolver.dto.deploy.DeployResponse;

public interface DeploymentService {

    DeployResponse deploy(Long projectId);
}
