package com.projects.resolver.controller;

import com.projects.resolver.dto.Project.ProjectRequest;
import com.projects.resolver.dto.Project.ProjectResponse;
import com.projects.resolver.dto.Project.ProjectSummaryResponse;
import com.projects.resolver.service.ProjectService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProjectController {

    ProjectService projectService;

    @GetMapping()
    public ResponseEntity<List<ProjectSummaryResponse>> getMyProjects(){
        Long userId = 1L;
        return ResponseEntity.ok(projectService.getUserProjects(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getMyProjectById(@PathVariable("id") Long projectId){
        Long userId = 1L;
        return ResponseEntity.ok(projectService.getUserProjectById(projectId,userId));
    }

    @PostMapping()
    public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest projectRequest){
        Long userId = 1L;
        return  ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(projectRequest,userId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable("id") Long projectId, @RequestBody @Valid ProjectRequest projectRequest){
        Long userId = 1L;
        return ResponseEntity.ok(projectService.updateProject(projectId,projectRequest,userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProjectResponse> deleteProject(@PathVariable("id") Long projectId){
        Long userId = 1L;
        projectService.softProject(projectId, userId);
        return ResponseEntity.noContent().build();
    }
}
