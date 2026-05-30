package com.projects.resolver.controller;

import com.projects.resolver.dto.Project.FileContentResponse;
import com.projects.resolver.dto.Project.FileNode;
import com.projects.resolver.dto.Project.FileTreeResponse;
import com.projects.resolver.service.ProjectFileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/api/projects/{projectId}/files")
@RequestMapping("/api/v1/workspace/projects/{projectId}/files")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FileController {

    ProjectFileService fileService;

    @GetMapping
    public ResponseEntity<FileTreeResponse> getFileTree(@PathVariable Long projectId){
        return ResponseEntity.ok(fileService.getFileTree(projectId));
    }

//    @GetMapping("/{*path}") // /src/hooks/AppHook.jsx
    @GetMapping("/content")
    public ResponseEntity<FileContentResponse> getFile(
            @PathVariable Long projectId, @RequestParam String path){
        return ResponseEntity.ok(fileService.getFileContent(projectId, path));
    }
}
