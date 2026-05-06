package com.projects.resolver.repositories;

import com.projects.resolver.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan,Long> {

    Optional<Plan> findByStripePriceId(String priceId);
}
