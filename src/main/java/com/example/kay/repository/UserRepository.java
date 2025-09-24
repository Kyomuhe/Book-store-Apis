package com.example.kay.repository;

import com.example.kay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :date")
    long countByCreatedAtAfter(@Param("date") LocalDateTime date);

    @Query("SELECT u FROM User u WHERE " +
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:enabled IS NULL OR u.enabled = :enabled)")
    Page<User> findUsersWithFilters(
            @Param("search") String search,
            @Param("role") User.Role role,
            @Param("enabled") Boolean enabled,
            Pageable pageable
    );

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    Page<User> findByUsernameContainingIgnoreCase(
            @Param("username") String username,
            Pageable pageable
    );

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    Page<User> findByEmailContainingIgnoreCase(
            @Param("email") String email,
            Pageable pageable
    );



}