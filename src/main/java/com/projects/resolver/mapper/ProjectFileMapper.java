package com.projects.resolver.mapper;

import com.projects.resolver.dto.Project.FileNode;
import com.projects.resolver.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

    List<FileNode> toListOfFileNode(List<ProjectFile> projectFileList);
}
