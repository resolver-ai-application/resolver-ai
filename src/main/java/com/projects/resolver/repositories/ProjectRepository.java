package com.projects.resolver.repositories;

import com.projects.resolver.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

    @Query(""" 
            SELECT p FROM Project p 
            WHERE p.deletedAt IS NULL
            AND EXISTS (
                SELECT 1 FROM ProjectMember pm
                WHERE pm.id.userId = :userId
                AND pm.id.projectId = p.id
            )
            ORDER BY p.updatedAt DESC
            """)
    List<Project> findAllAccesibleByUser(@Param("userId") Long userId);

    @Query("""
            SELECT p FROM Project p
            WHERE p.deletedAt IS NULL
            AND p.id = :projectId
            AND EXISTS (
                SELECT 1 FROM ProjectMember pm
                WHERE pm.id.userId = :userId
                AND pm.id.projectId = :projectId
            )
            """)
    Optional<Project> findAccessibleProjectById(@Param("userId") Long userId, @Param("projectId") Long projectId);
}
