package com.enigmacamp.enigmaCademy.model;

import com.enigmacamp.enigmaCademy.dto.request.CategoryRequest;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

}
