package com.enigmacamp.enigmaCademy.config;


import com.enigmacamp.enigmaCademy.model.User;
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
        if(!userRepository.existsByUsername("admin")) {
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
