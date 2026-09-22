## Pengenalan

Spring Security adalah framework yang menyediakan fitur komprehensif untuk mengamankan aplikasi Spring Boot. Pada modul ini, kita akan mempelajari konsep dasar Spring Security dan mengimplementasikan autentikasi dasar dengan HTTP Basic Authentication.

## Konsep Dasar Spring Security

### Authentication vs Authorization

1. **Authentication (Autentikasi)**
    - Proses memverifikasi identitas pengguna
    - Menjawab pertanyaan: "Siapa Anda?"
    - Biasanya menggunakan username/password, token, atau biometrik
2. **Authorization (Otorisasi)**
    - Proses menentukan apa yang boleh dilakukan oleh pengguna yang terautentikasi
    - Menjawab pertanyaan: "Apakah Anda boleh melakukan ini?"
    - Biasanya berdasarkan roles atau permissions

### Filter Chain

Spring Security bekerja sebagai rangkaian filter Servlet. Ketika permintaan masuk ke aplikasi:

1. Permintaan melewati rangkaian filter Spring Security
2. Setiap filter memeriksa aspek keamanan tertentu (autentikasi, otorisasi, dll)
3. Jika semua filter dilewati dengan sukses, permintaan dilanjutkan ke controller

### Komponen Utama Spring Security

1. **SecurityContextHolder** - Menyimpan detail autentikasi pengguna saat ini
2. **Authentication** - Mewakili token untuk permintaan autentikasi atau pengguna terautentikasi
3. **UserDetails** - Informasi inti pengguna
4. **UserDetailsService** - Service untuk mengambil informasi pengguna berdasarkan username
5. **GrantedAuthority** - Izin yang diberikan kepada pengguna (contoh: roles, permissions)

## Hands-on: Mengintegrasikan Spring Security

### Langkah 1: Tambahkan Dependensi Spring Security

Buka file `pom.xml` dan tambahkan dependensi Spring Security:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

```

### Langkah 2: Jalankan Aplikasi dan Perhatikan

Jalankan aplikasi setelah menambahkan dependensi Spring Security. Perhatikan bahwa:

1. Semua endpoint sekarang dilindungi secara default
2. Halaman login default muncul saat mengakses endpoint apa pun
3. Username default adalah `user`
4. Password acak digenerate dan terlihat di console log

### Langkah 3: Buat Konfigurasi Keamanan Dasar

Buat class `SecurityConfig` untuk mengonfigurasi Spring Security:

```java
package com.enigmacamp.enigmacademy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic();

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ADMIN")
                .build();

        UserDetails staff = User.builder()
                .username("student")
                .password(passwordEncoder().encode("student"))
                .roles("STUDENT")
                .build();

        UserDetails customer = User.builder()
                .username("instructor")
                .password(passwordEncoder().encode("instructor"))
                .roles("INSTRUCTOR")
                .build();

        return new InMemoryUserDetailsManager(admin, staff, customer);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### Langkah 4: Buat Controller untuk Publik

Buat controller sederhana untuk mendemonstrasikan endpoint publik:

```java
package com.enigmacamp.enigmaCademy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @GetMapping("/hello")
    public ResponseEntity<String> helloPublic() {
        return ResponseEntity.ok("Hello, public user!");
    }
}

```

### Langkah 5: Tambahkan Property untuk Spring Security

Tambahkan property berikut ke `application.properties` untuk melihat log debug Spring Security (opsional):
```
# Spring Security Log level
logging.level.org.springframework.security=DEBUG
```

## Pengujian

1. Akses endpoint publik:
    - `GET /api/v1/public/hello` - Tidak memerlukan autentikasi
2. Akses endpoint yang dilindungi tanpa autentikasi:
    - `GET /api/v1/users` - Akan mendapatkan error 401 Unauthorized
3. Akses endpoint yang dilindungi dengan Basic Auth:
    - `GET /api/v1/users` dengan header `Authorization: Basic YWRtaW46YWRtaW4=` (admin:admin)
    - Harusnya berhasil dengan respons 200 OK

## Penjelasan Konfigurasi

Mari kita bahas konfigurasi Spring Security lebih detail:

1. **@EnableWebSecurity**
    - Mengaktifkan dukungan Spring Security web
    - Mengimpor WebSecurityConfiguration, SpringWebMvcImportSelector, dll
2. **SecurityFilterChain**
    - Mendefinisikan filter mana yang harus digunakan dan dalam urutan apa
    - Mengkonfigurasi aturan keamanan
3. **http.csrf().disable()**
    - Menonaktifkan CSRF protection
    - Umum digunakan untuk REST API tanpa state
4. **authorizeHttpRequests()**
    - Mendefinisikan aturan otorisasi untuk HTTP requests
    - `.requestMatchers("/api/public/**").permitAll()` - URL ini dapat diakses tanpa autentikasi
    - `.anyRequest().authenticated()` - Semua URL lainnya memerlukan autentikasi
5. **httpBasic()**
    - Mengaktifkan HTTP Basic Authentication
    - Credentials dikirim dalam header Authorization sebagai Base64(username:password)
6. **InMemoryUserDetailsManager**
    - Implementasi dari UserDetailsService yang menyimpan user dalam memori
    - Baik untuk pengujian, bukan untuk production
7. **PasswordEncoder**
    - Mengenkripsi password untuk keamanan
    - BCrypt adalah algoritma hashing modern yang aman

## Kekurangan HTTP Basic Authentication

1. Credentials dikirim dengan setiap permintaan
2. Base64 encoding mudah didekode (bukan enkripsi)
3. Tidak ada cara standar untuk logout
4. Tidak cocok untuk aplikasi frontend modern

## Langkah Selanjutnya

Pada modul berikutnya (15B-security-entity), kita akan:

1. Membuat entity untuk User dan Role
2. Menyiapkan repository untuk model keamanan

## Latihan

1. Tambahkan user baru dengan custom role "MANAGER"
2. Buat endpoint baru yang hanya bisa diakses oleh user dengan role "STUDENT"
3. Ubah konfigurasi untuk membuat endpoint `/api/v1/users` bisa diakses oleh semua user yang terautentikasi
4. Ujilah semua perubahan dengan tool seperti Postman atau cURL

## Referensi

1. [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
2. [Basic Authentication](https://en.wikipedia.org/wiki/Basic_access_authentication)
3. [BCrypt Password Encoder](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/crypto/bcrypt/BCryptPasswordEncoder.html)