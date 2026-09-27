## Pengenalan

Pada langkah sebelumnya, kita telah mengimplementasikan autentikasi dengan JWT dan otorisasi berbasis peran. Namun, sebelum aplikasi siap untuk production, ada beberapa aspek keamanan tambahan yang perlu diimplementasikan. Pada modul ini, kita akan menambahkan konfigurasi CORS, security headers, error handling yang lebih baik, dan rate limiting untuk melindungi aplikasi dari berbagai serangan.

## Hands-on: Implementasi Fitur Keamanan Production

### Langkah 1: Konfigurasi CORS

CORS (Cross-Origin Resource Sharing) adalah mekanisme keamanan browser yang membatasi permintaan HTTP dari domain yang berbeda. Untuk mengintegrasikan API kita dengan frontend yang berjalan di domain berbeda, kita perlu mengkonfigurasi CORS.

Buat file `WebConfig.java` untuk konfigurasi CORS:

```java
package com.enigmacamp.enigmaCademy.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // izinkan origins tertentu
        corsConfiguration.setAllowedOrigins(Arrays.asList(allowedOrigins));

        // izinkan http method yang digunakan
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));

        // izinkan header tertentu
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization"));

        // izinkan kirim cookies dari domain berbeda
        corsConfiguration.setAllowCredentials(true);

        // izikan berapa lama browser menyimpan cache cors response
        corsConfiguration.setMaxAge(3600L);

        // izinkan expose authorization header ke client side
        corsConfiguration.setExposedHeaders(Collections.singletonList("Authorization"));

        // apply atau terapkan semua configurasi
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);
    }

}
```

### Tambahkan property CORS di application.properties:

```properties
# CORS Configuration
app.cors.allowed-origins=http://localhost:3000,https://mydomain.com
```
### Langkah 2: Tambahkan Security Headers

Security headers melindungi aplikasi dari berbagai serangan seperti XSS, clickjacking, content sniffing, dll. Tambahkan konfigurasi security headers di `SecurityConfig.java`:

```java
package com.enigmacamp.enigmaCademy.config;

import com.enigmacamp.enigmaCademy.security.jwt.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthTokenFilter authTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.deny()) // prevent clickjacking didalam iframes
                        .contentTypeOptions(Customizer.withDefaults()) // prebent MIME-sniffing
                        .xssProtection(
                                xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                        ) // xss
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; img-src 'self' data:; style-src 'self' 'unsafe-inline'; script-src 'self' 'unsafe-inline';")) //CSP
                        .referrerPolicy(referer -> referer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)) // mengcontrol referer header
                        .permissionsPolicyHeader(permissionsPolicy -> permissionsPolicy
                                .policy("camera=(), microphone=(), geolocation=()")) // pengaturan browser fitur
                )
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-resource/**",
                                "/swagger-ui.html",
                                "/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                        .anyRequest()
                        .authenticated());

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);

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

### Langkah 3: Implementasikan Error Handling Lebih Baik

Buat custom exception class untuk keamanan:

```java
package com.enigmacamp.enigmaCademy.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }
}
```

Tambahkan handler untuk exception keamanan di GlobalExceptionHandler:

```java
package com.enigmacamp.enigmaCademy.utils.exception;

import com.enigmacamp.enigmaCademy.dto.response.CommonResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Existing exception handlers...

    // Security-specific exception handlers

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {

        log.warn("Access denied for request to {}: {}", request.getRequestURI(), ex.getMessage());

        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .message("Anda tidak memiliki akses ke resource ini")
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<CommonResponse<Object>> handleDisabledException(
            DisabledException ex, HttpServletRequest request) {

        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message("Akun dinonaktifkan, silakan hubungi administrator")
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<CommonResponse<Object>> handleLockedException(
            LockedException ex, HttpServletRequest request) {

        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message("Akun terkunci, silakan coba lagi nanti atau hubungi administrator")
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CommonResponse<Object>> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {

        log.warn("Authentication exception for request to {}: {}",
                request.getRequestURI(), ex.getMessage());

        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message("Autentikasi gagal: " + ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
```

### Langkah 4: Implementasikan Rate Limiting (Opsional)

Rate limiting membatasi jumlah permintaan yang dapat dilakukan oleh client dalam periode waktu tertentu. Kita akan menggunakan bucket4j, library Java untuk rate limiting.

Tambahkan dependensi di pom.xml:

```xml
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.0.1</version>
</dependency>
```

Implementasikan rate limiter untuk endpoint autentikasi:

```java
package com.enigmacamp.enigmaCademy.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    // Store buckets by IP address
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Create a bucket for the IP address with a limit of 10 requests per minute
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

        // Only apply rate limiting to auth endpoints
        if (request.getRequestURI().startsWith("/api/auth/")) {
            // Get client IP
            String ipAddress = getClientIP(request);

            // Get or create bucket for this IP
            Bucket bucket = buckets.computeIfAbsent(ipAddress, k -> createNewBucket());

            // Try to consume a token
            if (bucket.tryConsume(1)) {
                // Token consumed, continue request
                filterChain.doFilter(request, response);
            } else {
                // No tokens available, return 429 Too Many Requests
                log.warn("Rate limit exceeded for IP: {}", ipAddress);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"statusCode\":429,\"message\":\"Rate limit exceeded. Please try again later.\"}");
            }
        } else {
            // Not an auth endpoint, let the request through
            filterChain.doFilter(request, response);
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Get the first IP in case of multiple proxies
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
```

Daftarkan filter ini di SecurityConfig:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // Existing configuration...

        // Add rate limit filter before authentication
        http.addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
}
```

## Penjelasan Fitur Keamanan

### 1. CORS (Cross-Origin Resource Sharing)

CORS adalah mekanisme keamanan browser yang membatasi permintaan dari domain yang berbeda dengan domain tempat resource berada.

**Mengapa Dibutuhkan?**

- Memungkinkan frontend dari domain berbeda mengakses API kita
- Mencegah akses dari domain yang tidak diizinkan
- Menentukan metode HTTP yang diizinkan

**Konfigurasi CORS yang Aman:**

- Tentukan origin yang diizinkan secara spesifik (hindari )
- Batasi metode HTTP yang diizinkan
- Batasi header yang diizinkan
- Kontrol apakah kredensial (cookies, auth headers) dapat dikirim

### 2. Security Headers

Security headers adalah header HTTP yang dapat ditambahkan ke respons untuk meningkatkan keamanan aplikasi.

**Headers yang Diimplementasikan:**

| Header                 | Fungsi                                               | Perlindungan Terhadap       |
|------------------------|------------------------------------------------------|-----------------------------|
| X-Frame-Options        | Mengontrol apakah page dapat ditampilkan dalam frame | Clickjacking                |
| X-XSS-Protection       | Mengaktifkan perlindungan XSS browser                | Cross-Site Scripting (XSS)  |
| X-Content-Type-Options | Mencegah MIME-sniffing                               | MIME Sniffing               |
| Content-Security-Policy| Membatasi sumber daya yang dapat dimuat              | XSS, Data Injection         |
| Referrer-Policy        | Mengontrol informasi Referer yang dikirim            | Information Leakage         |
| Permissions-Policy     | Membatasi fitur browser                              | Feature Abuse               |

### 3. Error Handling yang Lebih Baik

Error handling yang baik untuk keamanan:

- Memberikan pesan error yang informatif tanpa mengungkapkan detail teknis
- Memformat respons error yang konsisten
- Logging yang tepat untuk analisis security

**Security Error yang Ditangani:**

- BadCredentialsException - Username atau password salah
- AccessDeniedException - Tidak memiliki izin
- DisabledException - Akun dinonaktifkan
- LockedException - Akun terkunci
- AuthenticationException - Error autentikasi lainnya

### 4. Rate Limiting

Rate limiting membatasi jumlah permintaan yang dapat dilakukan dalam periode waktu tertentu.

**Manfaat Rate Limiting:**

- Mencegah serangan brute force login
- Melindungi dari DoS (Denial of Service)
- Mengurangi load server
- Membatasi penyalahgunaan API

**Algoritma Bucket dalam Rate Limiting:**

- Token bucket - Menyediakan jumlah token tertentu per periode waktu
- Setiap request menggunakan satu token
- Ketika token habis, request ditolak sampai refill

## Pengujian Fitur Keamanan

### 1. Testing CORS

Untuk menguji konfigurasi CORS:

- Buat request dari domain yang berbeda (misalnya aplikasi React di localhost:3000)
- Verifikasi bahwa request dari origin yang diizinkan diterima
- Verifikasi bahwa request dari origin yang tidak diizinkan ditolak

### 2. Testing Security Headers

Untuk menguji security headers:

- Gunakan tools seperti SecurityHeaders.io
- Verifikasi bahwa headers yang dikonfigurasi muncul di respons API
- Periksa efek headers seperti mencoba memuat API dalam iframe (seharusnya diblok)

### 3. Testing Error Handling

Untuk menguji error handling:

- Login dengan kredensial yang salah
- Akses resource yang memerlukan role yang tidak dimiliki
- Verifikasi bahwa respons error sesuai format dan informatif

### 4. Testing Rate Limiting

Untuk menguji rate limiting:

- Buat script untuk mengirim multiple login requests dalam waktu singkat
- Verifikasi bahwa setelah batas tercapai, API mengembalikan status 429 Too Many Requests
- Tunggu periode waktu reset dan verifikasi bahwa request kembali diterima

## Best Practices Keamanan Tambahan

### 1. Secure Development Lifecycle

- Lakukan security testing secara teratur
- Tinjau kode dari perspektif keamanan
- Perbarui dependensi secara rutin untuk mengatasi vulnerability
### 2. Environment-Specific Configuration

- Gunakan konfigurasi yang berbeda untuk development dan production
- Simpan secrets di environment variables atau secure store
- Nonaktifkan debug info di production

### 3. Logging dan Monitoring

- Log security events seperti login gagal, akses ditolak
- Atur alerts untuk pola mencurigakan
- Lakukan audit trail untuk tindakan sensitif

### 4. Deployment Security

- Gunakan HTTPS untuk semua komunikasi
- Terapkan continuous security scanning
- Gunakan container scanning jika menggunakan Docker

## Langkah Selanjutnya

Setelah menyelesaikan langkah ini, aplikasi LMS Sederhana kita sudah siap untuk production dari segi keamanan. Langkah selanjutnya bisa berupa:

1. Mengimplementasikan refresh token untuk perpanjangan sesi
2. Menambahkan multi-factor authentication
3. Mengimplementasikan audit logging komprehensif
4. Membangun frontend yang terintegrasi dengan API ini

## Referensi

1. [OWASP Top Ten](https://owasp.org/www-project-top-ten/)
2. [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html)
3. [CORS Reference](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)
4. [Content Security Policy](https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP)
5. [Bucket4j Rate Limiting](https://github.com/vladimir-bukhtoyarov/bucket4j)