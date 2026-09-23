## Pengenalan

Pada modul sebelumnya, kita telah mengimplementasikan autentikasi database menggunakan UserDetailsService kustom. Namun, metode autentikasi tersebut masih menggunakan HTTP Basic Authentication yang memiliki beberapa keterbatasan, seperti harus mengirimkan kredensial pada setiap request dan tidak ada mekanisme logout.

Pada modul ini, kita akan mengimplementasikan autentikasi berbasis token dengan JSON Web Token (JWT). JWT adalah standar industri (RFC 7519) untuk mewakili klaim dengan aman antara dua pihak.

## Konsep JWT (JSON Web Token)

### Struktur JWT

JWT terdiri dari tiga bagian yang dipisahkan oleh titik (.):

1. **Header** - Berisi tipe token dan algoritma hash yang digunakan
2. **Payload** - Berisi klaim (claims) atau data yang ingin disertakan dalam token
3. **Signature** - Digunakan untuk memverifikasi bahwa token tidak diubah

Format:

```text
header.payload.signature
```

Contoh:
```text
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

### Komponen JWT

1. **Header**

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

2. **Payload**

```json
{
  "sub": "1234567890",     // Subject (user ID)
  "name": "John Doe",      // Custom claim
  "roles": ["ROLE_USER"],  // User roles
  "iat": 1516239022,       // Issued At
  "exp": 1516242622        // Expiration Time
}
```

3. **Signature**

```json
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret
)
```

### Keuntungan JWT

1. **Stateless** - Server tidak perlu menyimpan token (berbeda dengan session)
2. **Compact** - Ukurannya kecil sehingga dapat dikirim melalui URL, POST parameter, atau header
3. **Self-contained** - Berisi semua informasi yang diperlukan tentang user
4. **Secure** - Dapat diverifikasi dan dienkripsi

## Hands-on: Mengimplementasikan JWT Utilities

### Langkah 1: Tambahkan Dependensi JWT

Buka file `pom.xml` dan tambahkan dependensi java-jwt:
```xml
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>4.4.0</version>
</dependency>
```

### Langkah 2: Konfigurasi Properties untuk JWT

Tambahkan konfigurasi JWT ke file `application.properties`:

```text
# JWT Configuration
app.jwt-secret=yourSecretKeyHereMakeItLongAndSecureYourSecretKeyHereMakeItLongAndSecure
app.jwt-expiration-ms=86400000
```

Catatan:

- `jwt-secret` adalah kunci yang digunakan untuk menandatangani token
- `jwt-expiration-ms` adalah waktu kedaluwarsa token dalam milidetik (86400000 ms = 24 jam)

### Langkah 3: Buat Kelas JwtUtils

Buat kelas baru bernama `JwtUtils` di package `com.enigmacamp.enigmaCademy.security.jwt`:

```java
package com.enigmacamp.enigmaCademy.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtUtils {
    // kita panggil informasi secret sign dan expired time
    @Value("${app.jwt-expiration-ms}")
    private int jwtExpirationMs;

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    // 1. generate jwt
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        // extract roles dari UserDetails
        String role = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("User tidak memiliki role"));

        // proses pembuatan jwt
        return JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date((new Date().getTime() + jwtExpirationMs)))
                .withClaim("role", role)
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    // 2. validasi token
    public boolean validateJwtToken(String token) {
        try {
            // hadirkan tukang verify token
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtSecret)).build();

            // verify token
            verifier.verify(token);

            return true;
        } catch (JWTVerificationException e) {
            log.error("JSON Web Token tidak valid : " + e.getMessage());
            return false;
        }
    }

    // 3. get username
    public String getUsernameFromJwtToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getSubject();
    }

    // 4. get role
    public String getRoleFromJwtToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("role").toString();
    }

}
```

## Penjelasan Kode

### 1. Generasi Token JWT

Method `generateJwtToken()` digunakan untuk membuat token JWT baru:

```java
public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        // extract roles dari UserDetails
        String role = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("User tidak memiliki role"));

        // proses pembuatan jwt
        return JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date((new Date().getTime() + jwtExpirationMs)))
                .withClaim("role", role)
                .sign(Algorithm.HMAC256(jwtSecret));
    }
```

Fungsi ini:

- Mengambil `UserDetails` dari objek `Authentication`
- Mengekstrak role pengguna dari authorities
- Membuat token dengan:
    - Subject (username pengguna)
    - Waktu pembuatan token (issuedAt)
    - Waktu kedaluwarsa token (expiresAt)
    - Custom claim untuk roles
    - Algoritma sign HMAC256 dengan secret key

### 2. Validasi Token JWT

Method `validateJwtToken()` digunakan untuk memverifikasi keabsahan token:

```java
    public boolean validateJwtToken(String token) {
        try {
            // hadirkan tukang verify token
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtSecret)).build();

            // verify token
            verifier.verify(token);

            return true;
        } catch (JWTVerificationException e) {
            log.error("JSON Web Token tidak valid : " + e.getMessage());
            return false;
        }
    }
```

Fungsi ini:

- Membuat verifier JWT dengan algoritma dan secret key yang sama
- Mencoba memverifikasi token
- Mengembalikan `true` jika token valid
- Mengembalikan `false` dan log error jika token tidak valid

### 3. Ekstraksi Informasi dari Token

Method `getUsernameFromJwtToken()` dan `getRolesFromJwtToken()` mengekstrak informasi dari token:

```java
    // 3. get username
    public String getUsernameFromJwtToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getSubject();
    }

    // 4. get role
    public String getRoleFromJwtToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("role").toString();
    }
```

Fungsi-fungsi ini:

- Mendekode token JWT tanpa verifikasi (verifikasi sudah dilakukan di method lain)
- Mengambil informasi seperti subject (username) dan custom claims (roles)

## Pengujian

Kita belum bisa menggunakan JWT secara langsung karena kita perlu mengimplementasikan filter dan controller autentikasi terlebih dahulu.

Pada modul berikutnya, kita akan melengkapi implementasi ini agar dapat menggunakan JWT untuk autentikasi.

## Langkah Selanjutnya

Pada modul berikutnya (14_Spring_Security_JWT_Filter), kita akan:
1. Mengimplementasikan `AuthTokenFilter` untuk memproses token JWT di setiap request
2. Mengimplementasikan `AuthEntryPointJwt` untuk menangani exception autentikasi
3. Mengintegrasikan filter JWT ke dalam security filter chain

## Latihan

1. Modifikasi `JwtUtils` untuk menambahkan claim tambahan seperti `email` atau `fullName`
2. Tambahkan metode untuk me-refresh token yang hampir kedaluwarsa
3. Implementasikan blacklist token untuk token yang di-revoke (hint: gunakan Redis atau in-memory cache)
4. Buat metode untuk mengekstrak semua claim dari token JWT

## Referensi

1. [JWT.io](https://jwt.io/) - Untuk debug dan mempelajari JWT
2. [java-jwt Documentation](https://github.com/auth0/java-jwt)

3. [RFC 7519: JSON Web Token](https://tools.ietf.org/html/rfc7519)
4. [Spring Security + JWT Tutorial](https://www.bezkoder.com/spring-boot-jwt-authentication/)
