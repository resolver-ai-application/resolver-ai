package com.projects.resolver.service.Impl;

import com.projects.resolver.dto.Project.FileContentResponse;
import com.projects.resolver.dto.Project.FileNode;
import com.projects.resolver.service.ProjectFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProjectFileServiceImpl implements ProjectFileService {

    @Override
    public List<FileNode> getFileTree(Long projectId, Long userId) {
        return List.of();
    }

    @Override
    public FileContentResponse getFileContent(Long projectId, String path, Long userId) {
        return null;
    }

    @Override
    public void saveFile(Long projectId, String filePath, String fileContent) {
        log.info("Saving file : {} ", filePath);
        //save file metadata in postgres
        //save file content in minio
    }
}
