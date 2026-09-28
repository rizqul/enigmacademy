package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.dto.request.SearchUserRequest;
import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.entity.User;
import com.enigmacamp.enigmaCademy.repository.UserRepository;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import com.enigmacamp.enigmaCademy.utils.exception.BadRequestException;
import com.enigmacamp.enigmaCademy.utils.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    // Code dibawah ini adalah dummy data agar data contoh User konsisten dan mudah dibaca di setiap test
    private User user(Long id, String name, String username, String email, UserRole role) {
        User user = new User(name, username, email, role, "password123");
        user.setId(id);
        return user;
    }

    private UserRequest userRequest(String role) {
        return new UserRequest(
                "Ipan", "ipan_unyu", "ipan@student.enigmacademy.com", "ipan123", role);
    }

    @Test
    void saveUser_shouldSaveUserAndReturnResponse() {
        // Arrange : Menyiapkan request dan tirukan hasil penyimpanan repository
        UserRequest request = userRequest("STUDENT");
        User savedUser = user(
                1L,
                "Ipan",
                "ipan_unyu",
                "ipan@student.enigmacademy.com",
                UserRole.STUDENT);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act : Menjalankan service yang akan diuji
        UserResponse response = userService.saveUser(request);

        // Assert : Memastikan data yang dikirim ke repository dan DTO hasilnya sesuai
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("Ipan", userCaptor.getValue().getName());
        assertEquals("ipan_unyu", userCaptor.getValue().getUsername());
        assertEquals(UserRole.STUDENT, userCaptor.getValue().getUserRole());
        assertEquals(1L, response.getId());
        assertEquals("Ipan", response.getName());
        assertEquals("ipan@student.enigmacademy.com", response.getEmail());
        assertEquals(UserRole.STUDENT, response.getUserRole());
    }

    @Test
    void saveUser_whenRoleIsBlank_shouldUseStudentRole() {
        // Arrange : Case ketika role kosong harus mengikuti aturan service: otomatis menjadi STUDENT
        UserRequest request = userRequest(" ");
        User savedUser = user(
                2L,
                "Ipan",
                "ipan_unyu",
                "ipan@student.enigmacademy.com",
                UserRole.STUDENT);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponse response = userService.saveUser(request);

        // Assert
        assertEquals(2L, response.getId());
        assertEquals("Ipan", response.getName());
        assertEquals("ipan_unyu", response.getUsername());
        assertEquals("ipan@student.enigmacademy.com", response.getEmail());
        assertEquals(UserRole.STUDENT, response.getUserRole());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void findAllUser_shouldReturnResponseForEveryUser() {
        // Arrange : Repository mengembalikan dua entity, lalu service mengubahnya menjadi DTO.
        when(userRepository.findAll()).thenReturn(List.of(
                user(1L, "Ipan", "ipan_unyu", "ipan@student.enigmacademy.com", UserRole.STUDENT),
                user(2L, "Admin", "admin", "admin@enigmacademy.com", UserRole.ADMIN)
        ));

        // Act
        List<UserResponse> responses = userService.findAllUser();

        // Assert
        assertEquals(2, responses.size());
        assertEquals("Ipan", responses.get(0).getName());
        assertEquals(UserRole.ADMIN, responses.get(1).getUserRole());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_whenUserExists_shouldReturnUserResponse() {
        // Arrange
        Long id = 1L;
        User foundUser = user(id, "Ipan", "ipan_unyu", "ipan@student.enigmacademy.com", UserRole.STUDENT);
        when(userRepository.findById(id)).thenReturn(Optional.of(foundUser));

        // Act
        UserResponse response = userService.getUserById(id);

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(foundUser.getName(), response.getName());
        assertEquals(foundUser.getEmail(), response.getEmail());
    }

    @Test
    void getUserById_whenUserDoesNotExist_shouldThrowResourceNotFoundException() {
        // Arrange : akan mengembalikan data kosong jika user dengan id tertentu tidak ditemukan di database
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void searchUsers_shouldReturnFilteredUsersAsResponses() {
        // Arrange
        SearchUserRequest request = new SearchUserRequest();
        User foundUser = user(1L, "Ipan", "ipan_unyu", "ipan@student.enigmacademy.com", UserRole.STUDENT);
        when(userRepository.findAll(org.mockito.ArgumentMatchers.<Specification<User>>any()))
                .thenReturn(List.of(foundUser));

        // Act
        List<UserResponse> responses = userService.searchUsers(request);

        // Assert
        assertEquals(1, responses.size());
        assertEquals("Ipan", responses.getFirst().getName());
        verify(userRepository).findAll(org.mockito.ArgumentMatchers.<Specification<User>>any());
    }

    @Test
    void updateById_shouldUpdateUserAndReturnResponse() {
        // Arrange
        Long id = 1L;
        User existingUser = user(
                id,
                "Nama Lama",
                "ipan_unyu",
                "lama@student.enigmacademy.com",
                UserRole.STUDENT);
        UserRequest request = new UserRequest(
                "Nama Baru",
                "ipan_unyu",
                "baru@student.enigmacademy.com",
                "passwordBaru",
                "STUDENT");
        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // Act
        UserResponse response = userService.updateById(id, request);

        // Assert
        assertEquals("Nama Baru", existingUser.getName());
        assertEquals("baru@student.enigmacademy.com", existingUser.getEmail());
        assertEquals("passwordBaru", existingUser.getPassword());
        assertEquals("Nama Baru", response.getName());
        assertEquals("baru@student.enigmacademy.com", response.getEmail());
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateById_whenIdIsNullOrNotPositive_shouldThrowBadRequestException() {
        // Assert : ID tidak valid ditolak sebelum service mengakses repository
        assertThrows(BadRequestException.class, () -> userService.updateById(null, userRequest("STUDENT")));
        assertThrows(BadRequestException.class, () -> userService.updateById(0L, userRequest("STUDENT")));
        assertThrows(BadRequestException.class, () -> userService.updateById(-1L, userRequest("STUDENT")));
        verifyNoInteractions(userRepository);
    }

    @Test
    void updateById_whenUserDoesNotExist_shouldThrowResourceNotFoundException() {
        // Arrange
        when(userRepository.findById(77L)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateById(77L, userRequest("STUDENT")));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteById_shouldDeleteUserAndReturnDeletedUserResponse() {
        Long id = 1L;
        User existingUser = user(id, "Ipan", "ipan_unyu", "ipan@student.enigmacademy.com", UserRole.STUDENT);
        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));

        UserResponse response = userService.deleteById(id);

        assertEquals(id, response.getId());
        assertEquals("Ipan", response.getName());
        verify(userRepository).deleteById(id);
    }

    @Test
    void deleteById_whenIdIsNullOrNotPositive_shouldThrowBadRequestException() {
        // Memastikan validasi ID berjalan sebelum query atau delete dipanggil.
        assertThrows(BadRequestException.class, () -> userService.deleteById(null));
        assertThrows(BadRequestException.class, () -> userService.deleteById(0L));
        assertThrows(BadRequestException.class, () -> userService.deleteById(-1L));
        verifyNoInteractions(userRepository);
    }

    @Test
    void deleteById_whenUserDoesNotExist_shouldThrowResourceNotFoundException() {
        when(userRepository.findById(77L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteById(77L));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void softDeleteById_shouldRenameUserSetDeletedAtAndSave() {
        Long id = 1L;
        User existingUser = user(id, "Ipan", "ipan_unyu", "ipan@student.enigmacademy.com", UserRole.STUDENT);
        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserResponse response = userService.softDeleteById(id);

        assertEquals("del_Ipan", existingUser.getName());
        assertNotNull(existingUser.getDeletedAt());
        assertEquals("del_Ipan", response.getName());
        verify(userRepository).save(existingUser);
    }

    @Test
    void softDeleteById_whenUserDoesNotExist_shouldThrowResourceNotFoundException() {
        when(userRepository.findById(88L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.softDeleteById(88L));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void searchUserWithPagination_shouldApplyRequestedPageSizeAndSort() {
        // Nomor halaman pada request dimulai dari 1, sedangkan PageRequest dimulai dari 0.
        SearchUserRequest request = SearchUserRequest.builder()
                .page(2)
                .size(5)
                .sortBy("email")
                .direction("desc")
                .build();
        User foundUser = user(1L, "Ipan", "ipan_unyu", "ipan@student.enigmacademy.com", UserRole.STUDENT);
        Page<User> repositoryPage = new PageImpl<>(List.of(foundUser), PageRequest.of(1, 5), 11);
        when(userRepository.findAll(
                org.mockito.ArgumentMatchers.<Specification<User>>any(),
                any(Pageable.class))).thenReturn(repositoryPage);

        Page<UserResponse> responsePage = userService.searchUserWithPagination(request);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userRepository).findAll(
                org.mockito.ArgumentMatchers.<Specification<User>>any(),
                pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();
        assertEquals(1, pageable.getPageNumber());
        assertEquals(5, pageable.getPageSize());
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor("email").getDirection());
        assertEquals(11, responsePage.getTotalElements());
        assertEquals("Ipan", responsePage.getContent().get(0).getName());
    }

    @Test
    void searchUserWithPagination_whenPagingOptionsAreMissing_shouldUseDefaults() {
        // Page=0, size=10, sort=name, direction=asc adalah nilai default service.
        SearchUserRequest request = new SearchUserRequest();
        when(userRepository.findAll(
                org.mockito.ArgumentMatchers.<Specification<User>>any(),
                any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        userService.searchUserWithPagination(request);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userRepository).findAll(
                org.mockito.ArgumentMatchers.<org.springframework.data.jpa.domain.Specification<User>>any(),
                pageableCaptor.capture());
        assertEquals(PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name")), pageableCaptor.getValue());
    }
}
