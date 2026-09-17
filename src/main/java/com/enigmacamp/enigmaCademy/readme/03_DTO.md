# 03.DTO - Data Transfer Objects

## Tujuan Pembelajaran
Pada branch ini, kita akan mempelajari konsep Data Transfer Objects (DTO) dan bagaimana DTO digunakan untuk memisahkan representasi data yang dikirim dan diterima oleh API.

## Konsep-konsep Penting

### Data Transfer Object (DTO)
DTO adalah objek yang dibuat khusus untuk keperluan transfer data antar lapisan aplikasi, terutama antara backend dan client. DTO sangat berguna untuk:

1. **Memisahkan format data API dari model internal** - Memungkinkan model internal berubah tanpa mempengaruhi API
2. **Melindungi data sensitif** - Memilih data mana yang dikirim ke client
3. **Mengoptimalkan transfer data** - Hanya mengirim data yang diperlukan
4. **Format data khusus** - Menyesuaikan format data untuk kebutuhan tertentu

### Jenis DTO
- **Request DTO** - Untuk menerima data dari client
- **Response DTO** - Untuk mengirim data ke client

## Apa yang akan kita buat
1. `MenuRequest` - DTO untuk menerima data menu dari client
2. `MenuResponse` - DTO untuk mengirim data menu ke client
3. Update controller untuk menggunakan DTO

## Implementasi
- Kita akan menambahkan package `dto` dengan subpackage `request` dan `response`
- Kita akan menyederhanakan proses konversi antara model dan DTO

## Struktur Package
```
com.learning.step_by_step
├── controller
│   └── UserController.java
├── dto
│   ├── request
│   │   └── UserRequest.java
│   └── response
│       └── UserResponse.java
├── model
│   └── User.java (dari branch sebelumnya)
└── reading
    └── 03_DTO.md
```