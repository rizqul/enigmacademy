## Pengenalan

Pada modul sebelumnya, kita telah membuat entity User dan Role untuk menyimpan data pengguna dalam database. Sekarang kita akan mengimplementasikan `UserDetailsService` kustom yang mengambil data pengguna dari database, mengkonfigurasi password encoding dengan BCrypt, dan mengintegrasikan komponen ini dengan Spring Security.

## Konsep UserDetailsService

### Peran UserDetailsService

`UserDetailsService` adalah interface inti dalam Spring Security yang bertanggung jawab untuk:

1. Mengambil informasi pengguna berdasarkan username
2. Mengubah data pengguna dari database menjadi objek `UserDetails`
3. Menyediakan mekanisme untuk validasi kredensial

Interface ini hanya memiliki satu method yang perlu diimplementasikan:
```java
UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
```

### Alur Autentikasi

Berikut adalah alur autentikasi saat menggunakan UserDetailsService:

1. Pengguna mengirimkan kredensial (username dan password)
2. Spring Security memanggil `loadUserByUsername(username)` untuk mendapatkan `UserDetails`
3. Spring Security membandingkan password yang dikirimkan dengan password yang diambil dari `UserDetails`
4. Jika cocok, autentikasi berhasil; jika tidak, autentikasi gagal

### PasswordEncoder

`PasswordEncoder` adalah interface untuk enkripsi password. Spring Security menyediakan beberapa implementasi, termasuk:

- `BCryptPasswordEncoder`: Menggunakan algoritma BCrypt (rekomendasi)
- `Pbkdf2PasswordEncoder`: Menggunakan algoritma PBKDF2
- `SCryptPasswordEncoder`: Menggunakan algoritma SCrypt

Dalam modul ini, kita akan menggunakan `BCryptPasswordEncoder` karena:

- Sangat aman dan tahan terhadap brute force attacks
- Secara otomatis menerapkan "salt" untuk setiap password
- Merupakan standard industri untuk enkripsi password

## Hands-on: Implementasi UserDetailsService

### Langkah 1: Implementasi UserDetailsService

Buat implementasi UserDetailsService yang menggunakan UserRepository:

```java
package com.enigmacamp.enigmaCademy.service.impl;

import com.learning.step_by_step.entity.User;
import com.learning.step_by_step.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Karena User entity kita sudah mengimplementasikan UserDetails,
        // kita bisa langsung mengembalikan User
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User dengan username: " + username + " tidak ditemukan"));
    }
}
```

### Langkah 2: Update SecurityConfig

Update SecurityConfig untuk menggunakan UserDetailsService kustom dan PasswordEncoder:
```java
package com.enigmacamp.enigmaCademy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests((authz) -> authz
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic();

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### Langkah 3: Buat Admin User Awal

Buat class untuk membuat admin user awal saat aplikasi pertama kali dijalankan:

```java
package com.enigmacamp.enigmaCademy.config;


import com.enigmacamp.enigmaCademy.entity.User;
import com.enigmacamp.enigmaCademy.repository.UserRepository;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void initAdminUser() {
        // validasi apakah admin sudah exist
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .name("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@enimacademy.com")
                    .userRole(UserRole.valueOf("ADMIN"))
                    .build();

            userRepository.save(admin);

            System.out.println("User admin sudah berhasil dibuat dengan \n username: admin / admin123");

        }
    }
}
```

### Langkah 4: Buat UserRegistrationService

Buat service untuk mendaftarkan pengguna baru:
```java
package com.enigmacamp.enigmaCademy.service;

import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;

public interface AuthService {
    UserResponse registerUser(UserRequest request);
}
```

Dan implementasinya:

```java
package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.mapper.UserMapper;
import com.enigmacamp.enigmaCademy.entity.User;
import com.enigmacamp.enigmaCademy.repository.UserRepository;
import com.enigmacamp.enigmaCademy.service.AuthService;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse registerUser(UserRequest request) {
        UserRole userRole = (request.getUserRole() != null && !request.getUserRole().isBlank())
                ? UserRole.valueOf(request.getUserRole())
                : UserRole.STUDENT;

        User newUser = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .userRole(userRole)
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(newUser);
        return UserMapper.convertToUserResponse(savedUser);
    }
}
```

### Langkah 5: Buat Controller untuk Registrasi

Buat controller untuk endpoint registrasi:
```java
package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.CommonResponse;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.service.AuthService;
import com.enigmacamp.enigmaCademy.utils.ResponseUtil;
import com.enigmacamp.enigmaCademy.utils.constants.ResponseMessage;
import com.enigmacamp.enigmaCademy.utils.validators.ValidationGroups;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService userRegistrationService;

    // create user baru
    @PostMapping("/register")
    public ResponseEntity<CommonResponse<UserResponse>> registerUser(
            @Validated(ValidationGroups.OnCreate.class)
            @RequestBody UserRequest request) {
        UserResponse userResponse = userRegistrationService.registerUser(request);

        return ResponseUtil.buildResponse(HttpStatus.CREATED, ResponseMessage.SUCCES_CREATE_DATA, userResponse);
    }
}
```

## Penjelasan Implementasi

### UserDetailsServiceImpl

- `UserDetailsServiceImpl` mengimplementasikan interface `UserDetailsService` dari Spring Security
- Method `loadUserByUsername` mengambil User dari database berdasarkan username
- Jika User tidak ditemukan, method melempar `UsernameNotFoundException`
- Karena entity User kita sudah mengimplementasikan `UserDetails`, kita bisa langsung mengembalikannya

### DaoAuthenticationProvider

- `DaoAuthenticationProvider` adalah implementasi `AuthenticationProvider` yang menggunakan DAO (UserDetailsService) untuk mengambil informasi pengguna
- Provider ini menggunakan `PasswordEncoder` untuk memverifikasi password
- Kita mengonfigurasinya dengan `UserDetailsService` dan `PasswordEncoder` kustom kita

### PasswordEncoder

- `BCryptPasswordEncoder` menggunakan algoritma BCrypt untuk mengenkripsi password
- BCrypt secara otomatis menambahkan "salt" yang unik untuk setiap password
- Password yang dienkripsi tidak bisa didekripsi (one-way hash)

### AdminInitializer

- Menggunakan anotasi `@PostConstruct` untuk menjalankan inisialisasi setelah bean dibuat
- Memeriksa apakah user admin sudah ada
- Jika belum, membuat user admin dengan role ADMIN
- Menggunakan `PasswordEncoder` untuk mengenkripsi password admin

### AuthService

- Menyediakan method untuk mendaftarkan pengguna baru
- Memeriksa apakah username dan email sudah digunakan
- Mengenkripsi password pengguna sebelum menyimpan ke database
- Memberikan role STUDENT secara default kepada pengguna baru

## Pengujian

### Pengujian Registrasi

1. Kirim permintaan POST ke `/api/public/auth/register` dengan body:
```java
{
    "name":"Student Baru",
    "username": "new_student",
    "email": "newStudent@student.enigmacademy.com",
    "password": "student123",
    "userRole": "STUDENT"
}
```

1. Anda akan mendapatkan respons 201 Created jika berhasil.

### Pengujian Autentikasi

1. Kirim permintaan ke endpoint yang dilindungi (misalnya `/api/v1/users`) tanpa autentikasi:
    - Harusnya mendapatkan 401 Unauthorized
2. Kirim permintaan ke endpoint yang dilindungi dengan autentikasi Basic:
    - Header: `Authorization: Basic Y3VzdG9tZXIxOnBhc3N3b3JkMTIz` (student:student123)
    - Harusnya berhasil dengan 200 OK
3. Kirim permintaan ke endpoint admin (jika ada) dengan user customer:
    - Harusnya mendapatkan 403 Forbidden

## Keamanan BCrypt

BCrypt adalah algoritma hashing password yang dirancang khusus untuk keamanan. Keunggulannya:

1. **Slow Hashing**: BCrypt sengaja dirancang untuk "lambat" secara komputasi, sehingga menghambat brute force attacks
2. **Salt Otomatis**: BCrypt secara otomatis menambahkan "salt" unik untuk setiap password
3. **Adaptive**: Kompleksitas BCrypt dapat disesuaikan seiring waktu dengan mengubah "work factor"

Contoh output hash BCrypt:
```java
$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
```
Bagian-bagiannya:

- `$2a$`: Menunjukkan algoritma BCrypt
- `10`: Work factor (semakin tinggi, semakin lambat dan aman)
- `N9qo8uLOickgx2ZMRZoMye`: Salt (22 karakter)
- `IjZAgcfl7p92ldGxad68LJZdL17lhWy`: Hash (31 karakter)

## Langkah Selanjutnya

Pada modul berikutnya (13_Spring_Security_JWT_Utils), kita akan:

1. Menambahkan dependensi java-jwt
2. Mengimplementasikan JwtUtils untuk pembuatan, validasi, dan ekstraksi informasi dari token
3. Mengonfigurasi property untuk JWT (secret key, expiration)

## Latihan

1. Tambahkan validasi tambahan pada registrasi (misalnya password strength)
2. Implementasikan endpoint untuk mengubah password
3. Buat endpoint admin untuk mengelola user (CRUD)
4. Tambahkan fitur untuk mengaktifkan/menonaktifkan akun user

## Referensi

1. [Spring Security UserDetailsService](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/core/userdetails/UserDetailsService.html)
2. [BCryptPasswordEncoder Documentation](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/crypto/bcrypt/BCryptPasswordEncoder.html)
3. [DaoAuthenticationProvider Documentation](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/authentication/dao/DaoAuthenticationProvider.html)
4. [Password Hashing Security](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html)