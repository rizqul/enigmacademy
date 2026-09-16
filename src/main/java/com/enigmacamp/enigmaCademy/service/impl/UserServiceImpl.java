package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.model.User;
import com.enigmacamp.enigmaCademy.repository.UserRepository;
import com.enigmacamp.enigmaCademy.service.UserService;
import com.enigmacamp.enigmaCademy.utils.enums.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        // validasi code

        return userRepository.save(user);
    }

    @Override
    public List<User> findAllUser() {
        // validasi code
        return userRepository.findAll();
    }

    @Override
    public List<User> findByName(String name) {
        // validasi code

        return userRepository.findByName(name);
    }

    @Override
    public List<User> findByEmail(String email) {
        // validasi code

        return userRepository.findByName(email);
    }

    @Override
    public List<User> findByNameContaining(String name) {
        // validasi code

        return  userRepository.findByNameContaining(name);
    }

    @Override
    public long countUserByRole(UserRole role) {
        // validasi code

        return userRepository.countByRole(role);
    }
}
