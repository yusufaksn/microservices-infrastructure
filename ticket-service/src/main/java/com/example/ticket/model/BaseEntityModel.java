package com.example.ticket.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntityModel implements Serializable {

    @CreatedDate
    @Column(name = "created_at")
    private Date cretedAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}