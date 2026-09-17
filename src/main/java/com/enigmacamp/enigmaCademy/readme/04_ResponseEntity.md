# 04.ResponseEntity - Standardisasi Response API

## Tujuan Pembelajaran
Pada branch ini, kita akan mempelajari cara standardisasi response API menggunakan ResponseEntity dan CommonResponse untuk meningkatkan konsistensi dan memberikan informasi yang lebih kaya kepada client.

## Konsep-konsep Penting

### ResponseEntity
`ResponseEntity` adalah kelas Spring yang memungkinkan kita mengontrol seluruh respons HTTP, termasuk:
- Status code (200 OK, 201 Created, 400 Bad Request, dll)
- Headers (Content-Type, Authorization, dll)
- Response body

Dengan ResponseEntity, kita dapat dengan mudah mengembalikan respons yang tepat untuk berbagai situasi.

### CommonResponse
CommonResponse adalah pola desain respons API yang menyediakan struktur konsisten untuk setiap respons, biasanya mencakup:
- Status code
- Pesan
- Data (payload)
- Informasi tambahan (waktu, pagination, dll)

Dengan menerapkan CommonResponse, client dapat dengan mudah memproses respons API secara konsisten.

## Apa yang akan kita buat
1. Kelas `CommonResponse<T>` untuk standardisasi semua respons API
2. Update controller untuk menggunakan ResponseEntity dengan CommonResponse
3. Penanganan status HTTP yang tepat untuk berbagai skenario

## Kelebihan Pendekatan Ini
- **Konsistensi** - Format respons yang sama di seluruh API
- **Informasi yang Kaya** - Lebih banyak informasi kontekstual untuk client
- **Penanganan Error** - Format respons yang konsisten bahkan saat terjadi error
- **Maintainability** - Memudahkan pemeliharaan dan pengembangan API

## Struktur Package
```
com.enigmaCademy
├── controller
│   └── UserController.java
├── dto
│   ├── request
│   │   └── UserRequest.java
│   └── response
│       ├── CommonResponse.java (baru)
│       └── UserResponse.java
└── model
    └── User.java
```

## Langkah Selanjutnya
Di branch berikutnya, kita akan mempelajari cara menangani error secara global dan konsisten di seluruh aplikasi.
