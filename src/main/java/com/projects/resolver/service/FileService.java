package com.projects.resolver.service;

import com.projects.resolver.dto.Project.FileContentResponse;
import com.projects.resolver.dto.Project.FileNode;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface FileService {
    List<FileNode> getFileTree(Long projectId, Long userId);

    FileContentResponse getFileContent(Long projectId, String path, Long userId);
}
