package com.projects.resolver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project_file")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectFile {

    Long id;
    Project project;
    String path;
    String minioObjectKey;
    Instant createdAt;
    Instant updatedAt;
    User createdBy;
    User modifiedBy;
}
