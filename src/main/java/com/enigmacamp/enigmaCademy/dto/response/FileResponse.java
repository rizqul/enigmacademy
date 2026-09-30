package com.enigmacamp.enigmaCademy.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name; // nama original dari file

    @Column(name = "file_name", nullable = false, unique = true)
    private String fileName; // nama file yang tersimpan didalam database

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "category")
    private String category;

    // digunakan untuk mengunduh file melalui endpoint yang memerlukan autentikasi
    @Column(name = "download_url")
    private String downloadUrl;

    // digunakan untuk mengakses file melalui endpoint public (tanpa auth)
    // contohnya gambar produk didalam ecommerce
    @Column(name = "public_url")
    private String publicUrl;

    // sebuah id untuk menandakan bahwa file tertentu mereferen terhadap data lain
    @Column(name = "reference_id")
    private String referenceId;
}
