package com.projects.resolver.controller;

import com.projects.resolver.dto.Project.ProjectRequest;
import com.projects.resolver.dto.Project.ProjectResponse;
import com.projects.resolver.dto.Project.ProjectSummaryResponse;
import com.projects.resolver.security.AuthUtil;
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
        return ResponseEntity.ok(projectService.getUserProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getMyProjectById(@PathVariable("id") Long projectId){
        return ResponseEntity.ok(projectService.getUserProjectById(projectId));
    }

    @PostMapping()
    public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest projectRequest){
        return  ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(projectRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable("id") Long projectId, @RequestBody @Valid ProjectRequest projectRequest){
        return ResponseEntity.ok(projectService.updateProject(projectId,projectRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProjectResponse> deleteProject(@PathVariable("id") Long projectId){
        projectService.softProject(projectId);
        return ResponseEntity.noContent().build();
    }
}
