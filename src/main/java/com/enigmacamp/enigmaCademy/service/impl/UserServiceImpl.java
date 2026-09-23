package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.dto.request.SearchUserRequest;
import com.enigmacamp.enigmaCademy.dto.request.UserRequest;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.mapper.UserMapper;
import com.enigmacamp.enigmaCademy.model.User;
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
public class UserServiceImpl implements UserService{

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

        if (id == null || id <= 0){
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
