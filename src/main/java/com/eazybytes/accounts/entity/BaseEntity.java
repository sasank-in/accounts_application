package com.eazybytes.accounts.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@ToString
@Getter @Setter
public class BaseEntity {

    @Column(insertable=false, updatable=false)
    private LocalDateTime createdAt;

    @Column(insertable=false)
    private String createdBy;

    @Column(insertable=false)
    private LocalDateTime updatedAt;

    @Column(insertable=false)
    private String updatedBy;

}
