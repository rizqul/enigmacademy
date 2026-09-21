package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.dto.request.SearchUserRequest;
import com.enigmacamp.enigmaCademy.dto.response.UserResponse;
import com.enigmacamp.enigmaCademy.mapper.UserMapper;
import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.repository.UserRepository;
import com.enigmacamp.enigmaCademy.service.UserService;
import com.enigmacamp.enigmaCademy.specification.UserSpecification;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // this is to implement IoC. for mark so the spring boot will know that this is needed to create
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = false) // use from the spring framework to implement EntityTransaction.
    public User saveUser(User user) {
        // Validation

        return userRepository.save(user);
    }

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public List<User> findByNameContaining(String name) {
        return userRepository.findByNameContaining(name);
    }

    @Override
    public long countByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

    @Override
    public List<UserResponse> searchUsers(SearchUserRequest request) {
        Specification<User> specification = UserSpecification.getSpecification(request);

        List<User> users =userRepository.findAll(specification);

        return UserMapper.convertToUserResponseList(users);
    }

    @Override
    public Page<UserResponse> searchUsersWithPagination(SearchUserRequest request) {
        // 1. page, size, sortBy, sortOrdering default values
        // for anticipate if front end didnt send the right request.
        int page = (request.getPage() == null || request.getPage() <= 0) ? 0 : request.getPage() - 1;
        int size = (request.getSize() == null || request.getSize() <= 0) ? 10 : request.getSize();
        String sortBy = (request.getSortBy() == null || request.getSortBy().isEmpty()) ? "id" : request.getSortBy();
        String sortOrder = (request.getSortOrder() == null || request.getSortOrder().isEmpty()) ? "asc" : request.getSortOrder();

        // 2. specification
        Specification<User> specification = UserSpecification.getSpecification(request);

        // 3. pageRequest and sorting method
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);


        Page<User> userPage = userRepository.findAll(specification, pageRequest);

        List<UserResponse> userResponses = UserMapper.convertToUserResponseList(userPage.getContent());

        return new PageImpl<>(userResponses, pageRequest, userPage.getTotalElements());
    }
}
