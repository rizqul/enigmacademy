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
//                        .permissionsPolicy(permissionsPolicy -> permissionsPolicy
//                                .policy("camera=(), microphone=(), geolocation=()")) // pengaturan browser fitur
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