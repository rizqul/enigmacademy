## Pengenalan

Pada modul sebelumnya, kita telah mengimplementasikan autentikasi dasar dengan in-memory user. Namun, untuk aplikasi yang sebenarnya, kita perlu menyimpan informasi pengguna dalam database. Pada modul ini, kita akan membuat entity untuk model keamanan dan mengimplementasikan relasi antara User dan Role.

### Model Entity untuk Keamanan

Dalam implementasi Spring Security dengan database, kita biasanya memerlukan setidaknya dua entity:

1. **User Entity**
    - Menyimpan informasi dasar pengguna (username, password, dll.)
    - Mengelola status akun (aktif, terkunci, kedaluwarsa, dll.)
2. **Role Entity**
    - Mewakili peran/otorisasi yang dapat dimiliki pengguna
    - Umumnya menggunakan format "ROLE_XXX" (misal: ROLE_ADMIN, ROLE_USER)

## Hands-on: Mengimplementasikan Model Keamanan

### Langkah 1: Membuat Enum untuk UserRole

Buat enum untuk menyimpan konstanta role:

```java
package com.enigmacamp.enigmaCademy.utils.enums;

public enum UserRole {
    ADMIN,
    INSTRUCTOR,
    STUDENT;

    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }

        for (UserRole userRole : UserRole.values()) {
            if (userRole.name().equals(value)) {
                return true;
            }
        }

        return false;
    }
}
```

Langkah 2: Membuat Entity User

```java
package com.enigmacamp.enigmaCademy.entity;

import jakarta.persistence.*;
import lombok.*;

import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String username;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private String password;

    public User(String name, String username, String email, UserRole userRole, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.userRole = userRole;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(userRole.name())
        );
    }
}
```

### Langkah 3: Membuat Repository untuk User dan Role

Buat interface repository untuk User:

```java
package com.enigmacamp.enigmaCademy.repository;

import com.enigmacamp.enigmaCademy.entity.User;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT COUNT(u) FROM User u WHERE u.userRole=:role")
    long countByRole(UserRole role);
}
```

### Langkah 4: Membuat Service untuk User

Buat interface UserService:

```java
package com.enigmacamp.enigmaCademy.service;

import com.enigmacamp.enigmaCademy.dto.request.SearchUserRequest;
import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    UserResponse saveUser(UserRequest userRequest);

    List<UserResponse> findAllUser();

    UserResponse getUserById(Long id);

    List<UserResponse> searchUsers(SearchUserRequest request);

    UserResponse updateById(Long id, UserRequest userRequest);

    UserResponse deleteById(Long id);

    UserResponse softDeleteById(Long id);

    Page<UserResponse> searchUserWithPagination(SearchUserRequest request);
}
```

Implementasi UserService:

```java
package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.dto.request.SearchUserRequest;
import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.mapper.UserMapper;
import com.enigmacamp.enigmaCademy.entity.User;
import com.enigmacamp.enigmaCademy.repository.UserRepository;
import com.enigmacamp.enigmaCademy.service.UserService;
import com.enigmacamp.enigmaCademy.specification.UserSpecification;
import com.enigmacamp.enigmaCademy.utils.constants.TableNames;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import com.enigmacamp.enigmaCademy.utils.exception.BadRequestException;
import com.enigmacamp.enigmaCademy.utils.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserResponse saveUser(UserRequest userRequest) {
        UserRole userRole = (userRequest.getUserRole() != null && !userRequest.getUserRole().isBlank())
                ? UserRole.valueOf(userRequest.getUserRole())
                : UserRole.STUDENT;

        User newUser = new User(
                userRequest.getName(),
                userRequest.getUsername(),
                userRequest.getEmail(),
                userRole,
                userRequest.getPassword());

        User savedUser = userRepository.save(newUser);
        return UserMapper.convertToUserResponse(savedUser);
    }

    @Override
    public List<UserResponse> findAllUser() {
        // validasi code
        List<User> users = userRepository.findAll();
        return UserMapper.convertToUserListResponse(users);
    }

    @Override
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", id));

        return UserMapper.convertToUserResponse(user);
    }

    // load User By Username
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<UserResponse> searchUsers(SearchUserRequest request) {
        // 1. get specification
        Specification<User> specification = UserSpecification.getSpecification(request);

        // 2. menjalankan query dengan specification
        List<User> users = userRepository.findAll(specification);

        // 3. mengubah hasil menjadi response DTOs
        return UserMapper.convertToUserListResponse(users);
    }

    @Override
    @Transactional
    public UserResponse updateById(Long id, UserRequest userRequest) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Id tidak boleh kosong atau lebih kecil/sama dengan nol");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", id));

        // id = 2, ganti selain email -> email tetap
        // id = 2, ganti email -> email di user lain exists?
        if (!Objects.equals(user.getEmail(), userRequest.getEmail())) {
            user.setEmail(userRequest.getEmail());
        }

        user.setName(userRequest.getName());
        user.setPassword(userRequest.getPassword());

        User newUser = userRepository.save(user);

        return UserMapper.convertToUserResponse(newUser);
    }

    @Override
    @Transactional
    public UserResponse deleteById(Long id) {

        if (id == null || id <= 0) {
            throw new BadRequestException("Id tidak boleh kosong atau lebih kecil/sama dengan nol");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user", "id", id));

        userRepository.deleteById(id);
        return UserMapper.convertToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse softDeleteById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(TableNames.USER, "id", id));

        user.setName("del_" + user.getName());
        user.setDeletedAt(LocalDateTime.now());

        User deletedUser = userRepository.save(user);
        return UserMapper.convertToUserResponse(deletedUser);
    }

    @Override
    public Page<UserResponse> searchUserWithPagination(SearchUserRequest request) {
        // 1. page, size, sortBy, dan direction dalam default values
        // untuk mengantisipasi kalau saat mengirimkan request tidak disertakan valuenya
        int page = (request.getPage() == null || request.getPage() <= 0) ? 0 : request.getPage() - 1;
        int size = (request.getSize() == null || request.getSize() <= 0) ? 10 : request.getSize();
        String sortBy = (request.getSortBy() == null || request.getSortBy().isEmpty()) ? "name" : request.getSortBy();
        String direction = (request.getDirection() == null || request.getDirection().isEmpty()) ? "asc"
                : request.getDirection();

        // 2. get specification
        Specification<User> specification = UserSpecification.getSpecification(request);

        // 3. bikin pageRequest
        // a. bikin sort
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        // b. bikin pageRequest
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<User> userPage = userRepository.findAll(specification, pageRequest);

        List<UserResponse> userResponses = UserMapper.convertToUserListResponse(userPage.getContent());

        return new PageImpl<>(userResponses, pageRequest, userPage.getTotalElements());
    }


}
```

## Penjelasan Implementasi

### Implementasi UserDetails

Class `User` mengimplementasikan interface `UserDetails` dari Spring Security, yang memiliki method-method berikut:

1. **getAuthorities()** - Mengembalikan kumpulan izin yang dimiliki oleh pengguna
2. **getPassword()** - Mengembalikan password pengguna
3. **getUsername()** - Mengembalikan username pengguna
4. **isAccountNonExpired()** - Memeriksa apakah akun masih valid
5. **isAccountNonLocked()** - Memeriksa apakah akun tidak terkunci
6. **isCredentialsNonExpired()** - Memeriksa apakah kredensial masih valid
7. **isEnabled()** - Memeriksa apakah akun diaktifkan

### Data Initialization

`DataInitializer` menggunakan anotasi `@PostConstruct` untuk menjalankan inisialisasi data segera setelah bean dibuat, memastikan role yang diperlukan sudah ada di database sebelum aplikasi mulai menerima permintaan.


## Langkah Selanjutnya

Pada modul berikutnya (12C_Spring_Security_userdetails), kita akan:

1. Mengimplementasikan UserDetailsService kustom yang menggunakan UserRepository
2. Mengonfigurasi Spring Security untuk menggunakan autentikasi database
3. Mengintegrasikan PasswordEncoder untuk enkripsi password

## Latihan

1. Tambahkan field tambahan pada entity User (seperti tanggal lahir, alamat, dll.)
2. Buat method tambahan pada UserRepository untuk mencari user berdasarkan kriteria tertentu
3. Tambahkan validasi pada entity User menggunakan Bean Validation
4. Buat entity tambahan yang berelasi dengan User (misalnya UserProfile atau UserPreference)

## Referensi

1. [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
2. [UserDetails Interface](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/core/userdetails/UserDetails.html)
3. [JPA Entity Relationships](https://www.baeldung.com/hibernate-one-to-many)