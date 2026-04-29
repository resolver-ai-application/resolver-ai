package com.projects.resolver.entity;

import com.projects.resolver.enums.PreviewStatus;
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
@Table(name = "preview")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Preview {

    Long id;
    Project project;
    String namespace;
    String podName;
    String previewUrl;
    PreviewStatus previewStatus;
    Instant startedAt;
    Instant terminatedAt;
    Instant createdAt;
}
