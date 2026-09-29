package com.enigmacamp.enigmaCademy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "file_storage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileStorage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name; // nama original dari file

    @Column(name = "file_name", nullable = false, unique = true)
    private String fileName; // nama file yang tersimpan didalam database

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "category")
    private String category;

    // sebuah id untuk menandakan bahwa file tertentu mereferen terhadap data lain
    @Column(name = "reference_id")
    private String referenceId;
}
