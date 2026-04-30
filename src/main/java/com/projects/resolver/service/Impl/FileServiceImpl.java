package com.projects.resolver.service.Impl;

import com.projects.resolver.dto.Project.FileContentResponse;
import com.projects.resolver.dto.Project.FileNode;
import com.projects.resolver.service.FileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public List<FileNode> getFileTree(Long projectId, Long userId) {
        return List.of();
    }

    @Override
    public FileContentResponse getFileContent(Long projectId, String path, Long userId) {
        return null;
    }
}
