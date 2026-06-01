package com.projects.resolver.repositories;

import com.projects.resolver.entity.UsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UsageLogRepository extends JpaRepository<UsageLog,Long> {

    Optional<UsageLog> findUserIdAndDate(Long userId, LocalDate today);
}
