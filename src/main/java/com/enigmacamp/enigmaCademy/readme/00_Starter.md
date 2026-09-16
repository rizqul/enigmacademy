# 00.Starter - Pengenalan Dasar Spring Boot

## Tujuan Pembelajaran
Pada branch ini, kita akan mempelajari dasar-dasar Spring Boot dan membuat aplikasi "Hello World" sederhana untuk proyek Restoran.

## Konsep-konsep Penting

### Inversion of Control (IoC)
IoC adalah prinsip desain perangkat lunak di mana kontrol alur program dialihkan dari program ke framework. Dalam Spring, IoC Container bertanggung jawab untuk membuat dan mengelola objek (beans).

### Dependency Injection (DI)
DI adalah implementasi dari IoC, di mana objek-objek yang dibutuhkan (dependencies) "disuntikkan" ke dalam objek lain yang membutuhkannya, bukan dibuat dalam objek tersebut.

### Spring Bean
Bean adalah objek yang diinstansiasi, dirakit, dan dikelola oleh Spring IoC container. Beans didefinisikan dalam konfigurasi (Java atau XML) yang digunakan oleh container.

### Application Context
ApplicationContext adalah interface yang menyediakan konfigurasi aplikasi. Ini membaca metadata bean dan menciptakan serta mengelola objek.

## Apa yang akan kita buat
1. Controller sederhana untuk menampilkan pesan sambutan dari restoran
2. Struktur dasar aplikasi Spring Boot untuk proyek Restoran

## File-file yang perlu diperhatikan
1. `pom.xml` - Konfigurasi Maven dan dependensi
2. `EnigmaCademyApplication.java` - Kelas utama aplikasi Spring Boot
3. `WelcomeController.java` - Controller sederhana untuk endpoint "Hello World"

## Langkah Selanjutnya
Di branch berikutnya (01.RestController), kita akan mempelajari lebih lanjut tentang RestController dan membuat endpoint API sederhana untuk mengelola menu restoran.
