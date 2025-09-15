package com.example.kay.repository;

import com.example.kay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :date")
    long countByCreatedAtAfter(@Param("date") LocalDateTime date);
}