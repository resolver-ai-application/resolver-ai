package com.projects.resolver.dto.Project;

import java.util.List;

public record FileTreeResponse(
        List<FileNode> files
) {
}
