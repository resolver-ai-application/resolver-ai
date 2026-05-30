package com.projects.resolver.service;

import com.projects.resolver.dto.Project.FileContentResponse;
import com.projects.resolver.dto.Project.FileNode;
import com.projects.resolver.dto.Project.FileTreeResponse;

import java.util.List;

public interface ProjectFileService {
    FileTreeResponse getFileTree(Long projectId);

    FileContentResponse getFileContent(Long projectId, String path);

    void saveFile(Long projectId, String filePath, String fileContent);
}
