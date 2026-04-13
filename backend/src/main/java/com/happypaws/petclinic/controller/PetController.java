package com.happypaws.petclinic.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication; 
import org.springframework.web.bind.annotation.*;

import com.happypaws.petclinic.entity.Owner;
import com.happypaws.petclinic.entity.Pet;
import com.happypaws.petclinic.entity.User;
import com.happypaws.petclinic.repository.OwnerRepository;
import com.happypaws.petclinic.repository.PetRepository;
import com.happypaws.petclinic.repository.UserRepository;
import com.happypaws.petclinic.service.PetService;

@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "http://localhost:3000") // ✅ Adjusted to specifically allow your React app
@Tag(name = "Pets", description = "Endpoints for managing pets")
public class PetController {

    private final PetService petService;
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;

    public PetController(PetService petService, 
                         UserRepository userRepository, 
                         OwnerRepository ownerRepository, 
                         PetRepository petRepository) {
        this.petService = petService;
        this.userRepository = userRepository;
        this.ownerRepository = ownerRepository;
        this.petRepository = petRepository;
    }

    // 🐾 1. GET MY PETS (Logged-in Owner Only)
    @Operation(summary = "Get my pets", description = "Returns all pets belonging to the authenticated owner")
    @ApiResponse(responseCode = "200", description = "List of pets returned")
    @GetMapping("/my-pets")
    public List<Pet> getMyPets(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Owner owner = ownerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Owner profile not found"));

        return petRepository.findByOwnerId(owner.getId());
    }

    @Operation(summary = "Create a pet", description = "Creates a new pet and assigns it to the authenticated owner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pet created successfully"),
        @ApiResponse(responseCode = "404", description = "Owner profile not found")
    })
    @PostMapping
    public Pet createPet(@RequestBody Pet pet, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        // Find the owner profile associated with this login
        Owner owner = ownerRepository.findByUserId(user.getId())
            .orElseThrow(() -> new RuntimeException("Owner profile not found"));

        pet.setOwner(owner); // Link pet to the logged-in owner
        return petService.savePet(pet);
    }

    @Operation(summary = "Get all pets", description = "Returns all pets (Admin/Vet use)")
    @ApiResponse(responseCode = "200", description = "List of all pets returned")
    @GetMapping
    public List<Pet> getAllPets() {
        return petService.getAllPets();
    }

    @Operation(summary = "Get pet by ID", description = "Returns a specific pet by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pet found"),
        @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    @GetMapping("/{id}")
    public Pet getPetById(@PathVariable Long id) {
        return petService.getPetById(id);
    }

    @Operation(summary = "Update pet", description = "Updates an existing pet's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pet updated successfully"),
        @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    @PutMapping("/{id}")
    public Pet updatePet(@PathVariable Long id, @RequestBody Pet petDetails) {
        Pet pet = petService.getPetById(id);

        pet.setName(petDetails.getName());
        pet.setType(petDetails.getType());
        pet.setBreed(petDetails.getBreed());
        pet.setBirthDate(petDetails.getBirthDate());
        pet.setGender(petDetails.getGender());
        
        // Detailed fields
        pet.setBiometrics(petDetails.getBiometrics());
        pet.setBehavioralProfile(petDetails.getBehavioralProfile());
        pet.setEnvironmentalContext(petDetails.getEnvironmentalContext());
        pet.setMedicalHistory(petDetails.getMedicalHistory());
        pet.setDietaryPreferences(petDetails.getDietaryPreferences());
        pet.setActivityLog(petDetails.getActivityLog());
        
        return petService.savePet(pet);
    }

    @Operation(summary = "Delete pet by ID", description = "Deletes a specific pet by its ID")
    @ApiResponse(responseCode = "200", description = "Pet deleted successfully")
    @DeleteMapping("/{id}")
    public String deletePetById(@PathVariable Long id) {
        petService.deletePet(id);
        return "Pet deleted successfully";
    }

    @Operation(summary = "Delete all pets", description = "Deletes all pets from the system")
    @ApiResponse(responseCode = "200", description = "All pets deleted")
    @DeleteMapping
    public String deleteAllPets() {
        petService.deleteAllPets();
        return "All pets deleted successfully";
    }

    @Operation(summary = "Bulk insert pets", description = "Creates multiple pets in a single request")
    @ApiResponse(responseCode = "200", description = "Pets created successfully")
    @PostMapping("/bulk")
    public List<Pet> createPets(@RequestBody List<Pet> pets) {
        return petService.saveAllPets(pets);
    }
}
