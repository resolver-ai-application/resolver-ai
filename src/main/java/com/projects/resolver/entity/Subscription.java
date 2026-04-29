package com.projects.resolver.entity;


import com.projects.resolver.enums.SubscriptionStatus;
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
@Table(name = "subscription")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subscription {

    Long id;
    User user;
    Plan plan;
    String stripeCustomerId;
    String stripeSubscriptionId;
    Instant currentPeriodStart;
    Boolean cancelAtPeriodEnd = false;
    Instant createdAt;
    Instant updatedAt;
    SubscriptionStatus status;
}
