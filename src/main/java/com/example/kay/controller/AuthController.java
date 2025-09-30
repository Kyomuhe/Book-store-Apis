package com.example.kay.controller;

import com.example.kay.dto.PaginationResponse;
import com.example.kay.dto.RefreshTokenRequest;
import com.example.kay.model.User;
import com.example.kay.service.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final WeeklySummaryService weeklySummaryService;
    private final CloudinaryService cloudinaryService;


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            User user = userService.createUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getRole()

            );

            String jwt = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);


//            // Send Welcome Email
//            try {
//                emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
//            } catch (Exception e) {
//                e.printStackTrace();
//                // Log the error but don't fail the signup process
//            }

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", jwt);
            response.put("refreshToken", refreshToken);
            response.put("user", createUserResponse(user));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();
            String jwt = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);



            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", jwt);
            response.put("refreshToken", refreshToken);
            response.put("user", createUserResponse(user));

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid username or password"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            final String refreshToken = request.getRefreshToken();
            final String username = jwtService.extractUsername(refreshToken);

            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid refresh token"));
            }

            UserDetails user = userService.loadUserByUsername(username);

            if (!jwtService.isTokenValid(refreshToken, user)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired refresh token"));
            }

            String newAccessToken = jwtService.generateToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("refreshToken", newRefreshToken);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token refresh failed: " + e.getMessage()));
        }
    }



    @PostMapping("/send-weekly-summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> testWeeklySummary() {
        weeklySummaryService.sendWeeklySummary();
        return ResponseEntity.ok("Weekly summary sent!");
    }

    @PostMapping("upload")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map> upload(@RequestParam("file") MultipartFile file) throws IOException {
        try {
            Map result = cloudinaryService.uploadFile(file);

            String publicId = result.get("public_id").toString();
            String thumbnailUrl = cloudinaryService.generateThumbnail(publicId);

            result.put("thumbnailUrl", thumbnailUrl);

            return ResponseEntity.ok(result);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Upload failed"));
        }
    }


    @GetMapping("display")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginationResponse<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) User.Role role,
            @RequestParam(required = false) Boolean enabled) {

        if (page < 0) page = 0;
        if (size < 1) size = 10;
        if (size > 100) size = 100;

        // Validating sortBy field
        String[] allowedSortFields = {"id", "username", "email", "firstName",
                "lastName", "role", "enabled", "createdAt", "updatedAt"};
        boolean validSortField = false;
        for (String field : allowedSortFields) {
            if (field.equals(sortBy)) {
                validSortField = true;
                break;
            }
        }
        if (!validSortField) {
            sortBy = "id";
        }

        PaginationResponse<User> response = userService.getAllUsers(
                page, size, sortBy, sortDirection, search, role, enabled);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/username")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PaginationResponse<User>> searchUsersByUsername(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        if (page < 0) page = 0;
        if (size < 1) size = 10;
        if (size > 100) size = 100;

        PaginationResponse<User> response = userService.searchUsersByUsername(
                username, page, size, sortBy, sortDirection);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/email")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PaginationResponse<User>> searchUsersByEmail(
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "email") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        if (page < 0) page = 0;
        if (size < 1) size = 10;
        if (size > 100) size = 100;

        PaginationResponse<User> response = userService.searchUsersByEmail(
                email, page, size, sortBy, sortDirection);

        return ResponseEntity.ok(response);
    }

    @GetMapping("search/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }


    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("username", user.getUsername());
        userResponse.put("email", user.getEmail());
        userResponse.put("firstName", user.getFirstName());
        userResponse.put("lastName", user.getLastName());
        userResponse.put("role", user.getRole());
        return userResponse;
    }

    //binds incoming JSON to Java objects
    @Data
    public static class SignupRequest {
        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String role;
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}