package com.projects.resolver.entity;

import com.projects.resolver.enums.ProjectRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project_members")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectMember {

    @EmbeddedId
    ProjectMemberId id;

    @ManyToOne
    @MapsId("projectId")
    Project project;

    @ManyToOne
    @MapsId("userId")
    User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    ProjectRole projectRole;

//    User invitedBy;
    Instant invitedAt;
    Instant acceptedAt;
}

