package com.enigmacamp.enigmaCademy.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// this abstract class is for create a column attribute that are redundant from each model. so in this ERD create_at and updated_at will create from BaseEntity

@MappedSuperclass  // this is the annotation that tell spring to implement this field attribute to each entity that need it
@EntityListeners(AuditingEntityListener.class) // this is a requirement also but still need to search it up later
public abstract class BaseEntity {
    @CreatedDate // to tell this is cretated at columng so spring will automatically save the date and time when it created
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // same but just for updated at
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
