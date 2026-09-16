package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.repository.UserRepository;
import com.enigmacamp.enigmaCademy.service.UserService;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // this is to implement IoC. for mark so the spring boot will know that this is needed to create
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional // use from the spring framework to implement EntityTransaction.
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
}
