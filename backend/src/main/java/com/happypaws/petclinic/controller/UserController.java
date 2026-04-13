package com.happypaws.petclinic.controller;

import com.happypaws.petclinic.entity.User;
import com.happypaws.petclinic.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Users", description = "Endpoints for managing system users (Admin only)")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ Constructor Injection
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Get all users", description = "Returns a list of all system users (Admin only)")
    @ApiResponse(responseCode = "200", description = "List of users returned")
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Operation(summary = "Create a user", description = "Creates a new system user with encrypted password (Admin only)")
    @ApiResponse(responseCode = "200", description = "User created successfully")
    @PostMapping
    public User createUser(@RequestBody User user) {
        // Encrypt the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Operation(summary = "Update a user", description = "Updates user information by ID (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        user.setRelatedEntityId(userDetails.getRelatedEntityId());

        // Only encrypt & update password if a new one is provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(user);
    }
    
    @Operation(summary = "Delete a user", description = "Deletes a user by ID (Admin only)")
    @ApiResponse(responseCode = "200", description = "User deleted")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
}
