package com.happypaws.petclinic.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.happypaws.petclinic.entity.User;
import com.happypaws.petclinic.entity.Vet;
import com.happypaws.petclinic.repository.UserRepository;
import com.happypaws.petclinic.repository.VetRepository;
import com.happypaws.petclinic.service.VetService;

@RestController
@RequestMapping("/api/vets")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Veterinarians", description = "Endpoints for managing veterinarians")
public class VetController {

    private final VetService vetService;
    private final UserRepository userRepository;
    private final VetRepository vetRepository; // ✅ Added Repository

    public VetController(VetService vetService, UserRepository userRepository, VetRepository vetRepository) {
        this.vetService = vetService;
        this.userRepository = userRepository;
        this.vetRepository = vetRepository;
    }

    @Operation(summary = "Get my vet profile", description = "Returns the profile of the authenticated veterinarian")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vet profile returned"),
        @ApiResponse(responseCode = "403", description = "Access denied - VET role required"),
        @ApiResponse(responseCode = "404", description = "Vet profile not found")
    })
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('VET')")
    public Vet getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        return vetRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Vet profile not found"));
    }

    @Operation(summary = "Create a veterinarian", description = "Creates a new vet profile (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vet created successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')") 
    public Vet createVet(@RequestBody Vet vet) {
        if (vet.getEmail() != null) {
            // Link to User Login if it exists
            userRepository.findByEmail(vet.getEmail())
                    .ifPresent(vet::setUser);
        }
        return vetService.createVet(vet);
    }

    @Operation(summary = "Get all veterinarians", description = "Returns a list of all veterinarians (public)")
    @ApiResponse(responseCode = "200", description = "List of vets returned")
    @GetMapping
    public List<Vet> getAllVets() {
        return vetService.getAllVets();
    }

    @Operation(summary = "Get vet by ID", description = "Returns a specific veterinarian by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vet found"),
        @ApiResponse(responseCode = "404", description = "Vet not found")
    })
    @GetMapping("/{id}")
    public Vet getVetById(@PathVariable Long id) {
        return vetService.getVetById(id);
    }

    @Operation(summary = "Update veterinarian", description = "Updates a vet's information (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vet updated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Vet updateVet(@PathVariable Long id, @RequestBody Vet vet) {
        return vetService.updateVet(id, vet);
    }

    @Operation(summary = "Delete veterinarian", description = "Deletes a vet by ID (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vet deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteVet(@PathVariable Long id) {
        vetService.deleteVet(id);
        return "Vet deleted successfully";
    }
}
