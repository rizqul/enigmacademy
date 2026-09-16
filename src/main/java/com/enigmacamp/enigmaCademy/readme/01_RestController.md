# 01.RestController - Dasar-dasar Pembuatan API

## Tujuan Pembelajaran
Pada branch ini, kita akan mempelajari konsep dasar pembuatan API dengan RestController dan cara mengimplementasikan CRUD operations (Create, Read, Update, Delete) untuk aplikasi Restoran.

## Konsep-konsep Penting

### @RestController
Anotasi yang menandakan bahwa kelas adalah controller yang menangani permintaan REST. Anotasi ini adalah kombinasi dari @Controller dan @ResponseBody, yang berarti nilai yang dikembalikan dari method akan langsung ditulis ke response body tanpa view resolution atau template rendering.

### @RequestMapping
Digunakan untuk memetakan permintaan HTTP ke method handler tertentu. Dapat diterapkan pada level kelas untuk URL dasar, dan pada level method untuk URL spesifik.

### HTTP Methods
- **@GetMapping** - Memetakan permintaan HTTP GET ke method tertentu
- **@PostMapping** - Memetakan permintaan HTTP POST ke method tertentu
- **@PutMapping** - Memetakan permintaan HTTP PUT ke method tertentu
- **@DeleteMapping** - Memetakan permintaan HTTP DELETE ke method tertentu

### Parameter Binding
- **@RequestParam** - Mengambil parameter dari query string URL (misalnya: `?nama=nasi`)
- **@PathVariable** - Mengambil nilai dari variabel path URL (misalnya: `/users/{id}`)
- **@RequestBody** - Mengkonversi request body JSON menjadi objek Java

### Konvensi Penamaan REST API
- Gunakan **bentuk jamak** untuk resource collection (misalnya: `/api/users` bukan `/api/user`)
- Gunakan **kata benda** untuk resource, bukan kata kerja
- Gunakan **lowercase** untuk URL
- Gunakan **kebab-case** untuk multi-kata (misalnya: `/api/course-categories`)

## Apa yang akan kita buat
1. Model `User` untuk merepresentasikan user dalam suatu EnigmaCademy Learning Management System
2. Controller untuk mengelola CRUD operations pada user
3. Implementasi endpoint API untuk:
    - Menampilkan semua user
    - Menampilkan detail user berdasarkan ID
    - Menambahkan user baru
    - Memperbarui user yang sudah ada
    - Menghapus user berdasarkan id

## Implementasi Sederhana
Pada branch ini, kita menggunakan implementasi sederhana dengan:
- List sebagai "database" sementara
- Return nilai langsung dari controller method
- Stream API untuk filter dan manipulasi data

## Konsep CRUD dan HTTP Methods

| Operasi | HTTP Method | Endpoint | Deskripsi |
|---------|-------------|----------|-----------|
| Create | POST | /api/users | Membuat user baru |
| Read | GET | /api/users | Mendapatkan semua user |
| Read | GET | /api/users/{id} | Mendapatkan user berdasarkan ID |
| Update | PUT | /api/users/{id} | Memperbarui user yang sudah ada |
| Delete | DELETE | /api/users/{id} | Menghapus user berdasarkan ID |

## Langkah Selanjutnya
Di branch berikutnya (02.Model-DTO), kita akan mempelajari lebih lanjut tentang pemisahan model domain dan Data Transfer Objects (DTO) untuk meningkatkan keamanan dan fleksibilitas API.

Pada branch yang lebih lanjut, kita akan membahas ResponseEntity untuk mengelola HTTP response dengan lebih baik, termasuk status code dan header.
