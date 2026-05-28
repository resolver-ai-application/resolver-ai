package com.projects.resolver.dto.Project;

import java.time.Instant;

public record FileNode(
        String path
) {

    @Override
    public String toString() {
        return path;
    }
}
