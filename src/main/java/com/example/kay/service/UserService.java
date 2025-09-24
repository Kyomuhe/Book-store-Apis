package com.example.kay.service;

import com.example.kay.dto.PaginationResponse;
import com.example.kay.model.User;
import com.example.kay.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

@Service
@Transactional
public class UserService implements UserDetailsService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    //registering a user
    public User createUser(String username, String email, String password, String firstName, String lastName) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(User.Role.USER);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

//getting the total number of users registered
    public long getTotalUserCount() {
        return userRepository.count();
    }

//getting the total number of users registered in the past 7 days
    public long getUsersCreatedInLastWeek() {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        return userRepository.countByCreatedAtAfter(weekAgo);
    }

//displaying users
    public PaginationResponse<User> getAllUsers(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            String search,
            User.Role role,
            Boolean enabled) {

        // Creating sort object
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);

        // Creating pageable object
        Pageable pageable = PageRequest.of(page, size, sort);

        // Getting paginated results with filters
        Page<User> userPage = userRepository.findUsersWithFilters(
                search, role, enabled, pageable);

        return PaginationResponse.from(userPage);
    }

//search by username
    public PaginationResponse<User> searchUsersByUsername(
            String username, int page, int size, String sortBy, String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userPage = userRepository.findByUsernameContainingIgnoreCase(
                username, pageable);

        return PaginationResponse.from(userPage);
    }

//search by email
    public PaginationResponse<User> searchUsersByEmail(
            String email, int page, int size, String sortBy, String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userPage = userRepository.findByEmailContainingIgnoreCase(
                email, pageable);

        return PaginationResponse.from(userPage);
    }

//getting user by id
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }


}