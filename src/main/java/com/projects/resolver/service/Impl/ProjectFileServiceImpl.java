package com.projects.resolver.service.Impl;

import com.projects.resolver.dto.Project.FileContentResponse;
import com.projects.resolver.dto.Project.FileNode;
import com.projects.resolver.entity.Project;
import com.projects.resolver.entity.ProjectFile;
import com.projects.resolver.exceptions.ResourceNotFoundException;
import com.projects.resolver.mapper.ProjectFileMapper;
import com.projects.resolver.repositories.ProjectFileRepository;
import com.projects.resolver.repositories.ProjectRepository;
import com.projects.resolver.service.ProjectFileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectFileServiceImpl implements ProjectFileService {

    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;
    private final MinioClient minioClient;
    private final ProjectFileMapper projectFileMapper;

    @Value("${minio.project-bucket}")
    private String projectBucket;

    private static final String BUCKET_NAME = "projects";

    @Override
    public List<FileNode> getFileTree(Long projectId) {
        List<ProjectFile> projectFileList = projectFileRepository.findByProjectId(projectId);
        return projectFileMapper.toListOfFileNode(projectFileList);
    }

    @Override
    public FileContentResponse getFileContent(Long projectId, String path) {
        String objectName = projectId + "/" + path;
        try(InputStream is=minioClient.getObject(GetObjectArgs.builder().bucket(BUCKET_NAME).object(objectName).build())){
            String content = new String(is.readAllBytes(),StandardCharsets.UTF_8);
            return new FileContentResponse(path,content);
        } catch(Exception e){
            log.error("Failed to read file: {}/{}",projectId,path, e);
            throw new RuntimeException("Failed to read file content",e);
        }
    }

    @Override
    public void saveFile(Long projectId, String filePath, String fileContent) {
        log.info("Saving file : {} ", filePath);
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ResourceNotFoundException("project",projectId.toString())
        );
        String cleanPath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        String objectKey = projectId + "/" + cleanPath;

        //save file content in minio: file content
        try{
            byte[] contentBytes = fileContent.getBytes(StandardCharsets.UTF_8);
            InputStream inputStream = new ByteArrayInputStream(contentBytes);
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(projectBucket)
                    .object(objectKey)
                    .stream(inputStream, contentBytes.length, -1)
                    .contentType(determineContentType(filePath))
                    .build());

            //save file metadata in postgres: file path
            ProjectFile file = projectFileRepository.findByProjectIdAndPath(projectId, cleanPath)
                    .orElseGet(() -> ProjectFile.builder()
                            .project(project)
                            .path(cleanPath)
                            .minioObjectKey(objectKey)
                            .createdAt(Instant.now())
                            .build());

            file.setUpdatedAt(Instant.now());
            projectFileRepository.save(file);
            log.info("Saved file {}", objectKey);
        } catch (Exception e){
            log.error("failed to save file: {}", objectKey);
            throw  new RuntimeException("Failed to save file", e);
        }
    }

    private String determineContentType(String path){
        String type = URLConnection.guessContentTypeFromName(path);
        if(type!=null) return type;
        if(path.endsWith(".js") || path.endsWith(".jsx") || path.endsWith(".ts") || path.endsWith(".tsx")) return "text/javascript";
        if(path.endsWith(".json")) return "application/josn";
        if(path.endsWith(".css")) return "text/css";
        return "text/plain";
    }
}
