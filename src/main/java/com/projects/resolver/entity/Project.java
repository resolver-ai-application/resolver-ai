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
@Table(name = "project")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Project {

    Long id;
    String name;
    User owner;
    Boolean isPublic;
    Instant createdAt;
    Instant updatedAt;
    Instant deletedAt;
}
