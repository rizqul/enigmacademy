## Pengenalan

Pada file sebelumnya, kita telah mengimplementasikan utilitas JWT dan filter untuk memproses token. Namun, kita belum memiliki endpoint untuk pengguna melakukan login dan mendapatkan token JWT. Pada modul ini, kita akan mengimplementasikan endpoint autentikasi untuk login dan registrasi pengguna.

## Hands-on: Implementasi Auth Endpoints

### Langkah 1: Buat DTO untuk Request dan Response

Pertama, kita perlu membuat DTO untuk request login dan registrasi, serta DTO untuk response.

**LoginRequest.java**:

```java
package com.enigmacamp.enigmaCademy.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "username tidak boleh kosong")
    private String username;

    @NotBlank(message = "Password Tidak Boleh Kosong")
    private String password;
}
```

### SignupRequest.java:
```java
package com.enigmacamp.enigmaCademy.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {
    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 3, max = 20, message = "Username harus antara 3-20 karakter")
    private String username;

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 6, max = 40, message = "Password harus antara 6-40 karakter")
    private String password;

    private String fullName;

    @NotBlank
    @ValidUserRole(groups = {ValidationGroups.OnCreate.class})
    private String userRole;
}
```

### JwtResponse.java:
```java
package com.enigmacamp.enigmaCademy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String role;
    private String username;
}
```

### Langkah 2: Buat Service untuk Autentikasi

Sekarang, kita akan membuat interface dan implementasi untuk service autentikasi:

**AuthService.java**:
```java
package com.enigmacamp.enigmaCademy.service;

import com.enigmacamp.enigmaCademy.dto.request.LoginRequest;
import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.LoginResponse;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import org.springframework.security.authentication.BadCredentialsException;

public interface AuthService {
    LoginResponse loginUser(LoginRequest request) throws BadCredentialsException;

    UserResponse registerUser(UserRequest request);
}
```

### AuthServiceImpl.java:

```java
package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.dto.request.LoginRequest;
import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.LoginResponse;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.mapper.UserMapper;
import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.repository.UserRepository;
import com.enigmacamp.enigmaCademy.security.jwt.JwtUtils;
import com.enigmacamp.enigmaCademy.service.AuthService;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // kebutuhan auth
    private final AuthenticationManager authenticationManager;

    // untuk generate token
    private final JwtUtils jwtUtils;

    @Override
    public LoginResponse loginUser(LoginRequest request) throws BadCredentialsException {
        // authenticare menggunakan authenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // set authentication didalam security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate jwt token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // get role
        String role = jwtUtils.getRoleFromJwtToken(jwt);

        // return response
        return LoginResponse.builder()
                .token(jwt)
                .role(role)
                .username(request.getUsername())
                .build();
    }

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

### Langkah 3: Buat Controller untuk Autentikasi

Sekarang, kita akan membuat controller untuk endpoint autentikasi:

```java
package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.dto.request.LoginRequest;
import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.CommonResponse;
import com.enigmacamp.enigmaCademy.dto.response.LoginResponse;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.service.AuthService;
import com.enigmacamp.enigmaCademy.utils.ResponseUtil;
import com.enigmacamp.enigmaCademy.utils.constants.ResponseMessage;
import com.enigmacamp.enigmaCademy.utils.validators.ValidationGroups;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    // register user baru
    @PostMapping("/register")
    public ResponseEntity<CommonResponse<UserResponse>> registerUser(
            @Validated(ValidationGroups.OnCreate.class)
            @RequestBody UserRequest request) {
        UserResponse userResponse = authService.registerUser(request);

        return ResponseUtil.buildResponse(HttpStatus.CREATED, ResponseMessage.SUCCES_CREATE_DATA, userResponse);
    }

    // login user
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponse>> loginUser(
            @Valid
            @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.loginUser(request);

        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCES_LOGIN, loginResponse);
    }
}
```

## Penjelasan Alur Autentikasi

### 1. Login Flow

```text
sequenceDiagram
    participant Client
    participant AuthController
    participant AuthService
    participant AuthManager
    participant UserDetailsService
    participant JwtUtils

    Client->>AuthController: POST /api/auth/login
    AuthController->>AuthService: authenticateUser(LoginRequest)
    AuthService->>AuthManager: authenticate(UsernamePasswordAuthenticationToken)
    AuthManager->>UserDetailsService: loadUserByUsername(username)
    UserDetailsService-->>AuthManager: UserDetails
    AuthManager-->>AuthService: Authentication
    AuthService->>JwtUtils: generateJwtToken(Authentication)
    JwtUtils-->>AuthService: JWT Token
    AuthService-->>AuthController: JwtResponse
    AuthController-->>Client: 200 OK + JWT Token
```

![Login Flow](../img/img_5.png)

### 2. Registration Flow

```java
sequenceDiagram
participant Client
participant AuthController
participant AuthService
participant UserService
participant RoleService
participant PasswordEncoder

Client->>AuthController: POST /api/auth/register
AuthController->>AuthService: registerUser(SignupRequest)
AuthService->>UserService: existsByUsername(username)
UserService-->>AuthService: boolean
AuthService->>UserService: existsByEmail(email)
UserService-->>AuthService: boolean

alt Username or Email exists
AuthService-->>AuthController: BadRequestException
AuthController-->>Client: 400 Bad Request
    else Username and Email are new
AuthService->>PasswordEncoder: encode(password)
PasswordEncoder-->>AuthService: encodedPassword
AuthService->>RoleService: getOrCreate(ROLE_CUSTOMER)
RoleService-->>AuthService: Role
AuthService->>UserService: save(User)
UserService-->>AuthService: User
AuthService-->>AuthController: void
AuthController-->>Client: 201 Created
        end
```

![Login Flow](../img/img_6.png)

## Aspek Keamanan Penting

Beberapa aspek keamanan yang diimplementasikan dalam kode di atas:

1. **Input Validation**
    - `@Valid` anotasi untuk memvalidasi DTO request
    - `@NotBlank`, `@Size`, `@Email` untuk validasi field
2. **Password Encoding**
    - Menggunakan `PasswordEncoder` untuk mengenkripsi password
    - Password tidak pernah disimpan sebagai plaintext
3. **Role-Based Access**
    - User diberikan role default (CUSTOMER) jika tidak ditentukan
    - Validasi role yang valid
4. **Exception Handling**
    - Menggunakan exception kustom untuk kasus bisnis
    - Pesan error yang informatif tetapi tidak terlalu terperinci

## Pengujian Endpoint Autentikasi

Setelah mengimplementasikan endpoint autentikasi, kita dapat menguji dengan Postman atau cURL.

### 1. Registrasi User Baru

**Request:**

```text
POST /api/auth/register
Content-Type: application/json

{
    "username": "customer1",
    "email": "customer1@example.com",
    "password": "password123",
    "fullName": "Customer User"
}
```

**Response:**

```json
{
    "statusCode": 201,
    "message": "Registrasi berhasil",
    "data": "User berhasil didaftarkan!",
    "timestamp": "2025-04-29T09:30:15.123"
}
```

### 2. Login User

**Request:**

```json
POST /api/auth/login
Content-Type: application/json

{
    "username": "customer1",
    "password": "password123"
}
```

**Response:**

```json
{
    "statusCode": 200,
    "message": "Login berhasil",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "type": "Bearer",
        "username": "customer1",
        "role": "ROLE_CUSTOMER"
    }
}
```

### 3. Mengakses Endpoint yang Dilindungi

**Request:**

```text
GET /api/menus
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## Praktik Terbaik untuk Auth Endpoints

1. **Rate Limiting**
    - Batasi jumlah percobaan login dalam interval waktu tertentu
    - Implementasikan cooldown setelah beberapa kali gagal login
2. **Secure Password Policy**
    - Terapkan kebijakan password yang kuat (panjang, kombinasi karakter)
    - Pertimbangkan menggunakan bcrypt dengan konfigurasi yang kuat (work factor)
3. **Token Management**
    - Gunakan waktu kedaluwarsa token yang masuk akal
    - Implementasikan token refresh untuk pengalaman pengguna yang lebih baik
4. **Activity Logging**
    - Log aktivitas login (berhasil dan gagal)
    - Alert untuk aktivitas mencurigakan

## Langkah Selanjutnya

Pada langkah berikutnya, kita akan:

1. Mengimplementasikan role-based access control untuk endpoint aplikasi
2. Mengkonfigurasi aturan akses berdasarkan role pengguna
3. Menerapkan pembatasan akses pada level endpoint

## Latihan

1. Tambahkan fitur verifikasi email setelah registrasi
2. Implementasikan endpoint untuk reset password
3. Tambahkan token refresh untuk memperpanjang sesi pengguna
4. Implementasikan multi-factor authentication

## Referensi

1. [Spring Security Authentication](https://docs.spring.io/spring-security/reference/servlet/authentication/index.html)
2. [Spring Boot Validation](https://www.baeldung.com/spring-boot-bean-validation)
3. [JWT Best Practices](https://auth0.com/blog/a-look-at-the-latest-draft-for-jwt-bcp/)
4. [Spring Security Password Encoding](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html)