package com.sascom.chickenstock.global.entity;


import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {
//    @CreatedDate
    private LocalDateTime createdAt;
//    @LastModifiedDate
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @PrePersist
    public void updateCreatedAt() {
        this.createdAt = LocalDateTime.now().withNano(0);
    }

    @PreUpdate
    public void updateUpdatedAt() {
        this.updatedAt = LocalDateTime.now().withNano(0);
    }

    @PreRemove
    protected void onDelete() {
        deletedAt = LocalDateTime.now().withNano(0);
    }
}
