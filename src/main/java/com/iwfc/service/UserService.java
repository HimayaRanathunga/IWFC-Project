package com.iwfc.service;

import com.iwfc.exception.DuplicateEntityException;
import com.iwfc.model.Role;
import com.iwfc.model.User;
import com.iwfc.pattern.behavioural.NotificationCenter;
import com.iwfc.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * [OOP / User management]
 * Registers and looks up users (Administrator, Instructor, Member).
 * Single responsibility: user rules only. A newly registered user is also
 * subscribed to the NotificationCenter (Observer pattern).
 */
public class UserService {

    // Dependency inversion: Repository interface. Composition: uses the shared NotificationCenter.
    private final Repository<User, String> userRepository;
    private final NotificationCenter notificationCenter;

    // Constructor injection of both collaborators.
    public UserService(Repository<User, String> userRepository, NotificationCenter notificationCenter) {
        this.userRepository = userRepository;
        this.notificationCenter = notificationCenter;
    }

    public void registerUser(User user) {
        if (userRepository.existsById(user.getUsername())) {
            throw new DuplicateEntityException("Username " + user.getUsername() + " is already taken");
        }
        userRepository.add(user);
        // Observer: User is a NotificationObserver, so it can subscribe (polymorphism).
        notificationCenter.registerObserver(user);
    }

    /** Optional: the caller must handle the case where no user exists. */
    public Optional<User> findByUsername(String username) {
        return userRepository.findById(username);
    }

    public List<User> findByRole(Role role) {
        // Streams: filter with a lambda; toList() returns an unmodifiable list.
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == role)
                .toList();
    }

    public List<User> listAll() {
        return userRepository.findAll();
    }
}
