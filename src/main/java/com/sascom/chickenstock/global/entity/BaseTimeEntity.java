package com.sascom.chickenstock.global.entity;


import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreRemove;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {
    @CreatedDate
    private Timestamp createdAt;
    @LastModifiedDate
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    @PreRemove
    protected void onDelete() {
        Instant instant = LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant();
        deletedAt = Timestamp.from(instant);
    }
}
