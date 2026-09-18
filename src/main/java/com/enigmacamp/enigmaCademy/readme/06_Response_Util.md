# 06.ResponseUtil - Standardisasi Response dan Clean Code

## Tujuan Pembelajaran
Pada branch ini, kita akan:
1. Memindahkan logika pembuatan ResponseEntity ke dalam kelas utility
2. Memisahkan logic mapping antara Model dan DTO ke dalam class Mapper
3. Menerapkan prinsip Clean Code dengan struktur code yang lebih rapi

## Konsep Penting

### Utility Class untuk ResponseEntity
Menggunakan utility class memiliki beberapa keuntungan:
- Mengurangi duplikasi kode di berbagai controller
- Memudahkan perubahan format response secara terpusat
- Meningkatkan konsistensi format response di seluruh aplikasi

### Mapper Pattern
Memisahkan logika konversi antara Model dan DTO:
- Membuat controller lebih bersih (tidak ada logic mapping di controller)
- Memudahkan perubahan format DTO tanpa mengubah controller
- Mengikuti prinsip Single Responsibility Principle

### Clean Code Principles
- Menyusun kode dengan rapi (private methods di bagian bawah)
- Memberikan nama yang jelas untuk method dan variabel
- Memisahkan kode berdasarkan fungsinya

## Apa yang telah kita buat
1. Kelas `ResponseUtil` dengan metode statis untuk membuat ResponseEntity
2. Kelas `UserMapper` untuk mengkonversi antara Model dan DTO
3. Restrukturisasi controller dengan private methods di bagian bawah

## Struktur Package
```
com.enigmacamp.enigmaCademy
├── constant
│   └── ResponseMessage.java
├── controller
│   └──  UserController.java
├── dto
│   ├── request
│   │   └── UserRequest.java
│   └── response
│       └── CommonResponse.java
├── mapper
│   └── UserMapper.java
├── model
│   └── User.java
└── util
    └── ResponseUtil.java
```