package com.iwfc.service;

import com.iwfc.exception.DuplicateEntityException;
import com.iwfc.model.Role;
import com.iwfc.model.User;
import com.iwfc.pattern.behavioural.NotificationCenter;
import com.iwfc.repository.Repository;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final Repository<User, String> userRepository;
    private final NotificationCenter notificationCenter;

    public UserService(Repository<User, String> userRepository, NotificationCenter notificationCenter) {
        this.userRepository = userRepository;
        this.notificationCenter = notificationCenter;
    }

    public void registerUser(User user) {
        if (userRepository.existsById(user.getUsername())) {
            throw new DuplicateEntityException("Username " + user.getUsername() + " is already taken");
        }
        userRepository.add(user);
        notificationCenter.registerObserver(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findById(username);
    }

    public List<User> findByRole(Role role) {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == role)
                .toList();
    }

    public List<User> listAll() {
        return userRepository.findAll();
    }
}
