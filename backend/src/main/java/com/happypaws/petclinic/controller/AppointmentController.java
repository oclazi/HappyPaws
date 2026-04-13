package com.happypaws.petclinic.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.happypaws.petclinic.dto.AppointmentRequest;
import com.happypaws.petclinic.entity.*;
import com.happypaws.petclinic.repository.*;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Appointments", description = "Endpoints for managing appointments")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;
    private final VetRepository vetRepository;

    public AppointmentController(AppointmentRepository appointmentRepository, UserRepository userRepository, OwnerRepository ownerRepository, PetRepository petRepository, VetRepository vetRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.ownerRepository = ownerRepository;
        this.petRepository = petRepository;
        this.vetRepository = vetRepository;
    }

    @Operation(summary = "Get all appointments", description = "Returns all appointments (Admin use)")
    @ApiResponse(responseCode = "200", description = "List of appointments returned")
    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Operation(summary = "Book an appointment", description = "Books a new appointment for the authenticated owner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointment booked successfully"),
        @ApiResponse(responseCode = "404", description = "Owner profile not found"),
        @ApiResponse(responseCode = "409", description = "Time slot unavailable")
    })
    @PostMapping
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequest request, Authentication authentication) {
        String email = authentication.getName();
        
        // Find Owner by EMAIL
        Owner owner = ownerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Owner profile not found for email: " + email));

        // Check availability
        boolean isSlotTaken = appointmentRepository.existsByVetIdAndDateAndTime(
            request.getVetId(), request.getDate(), request.getTime()
        );

        if (isSlotTaken) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Time slot unavailable.");
        }

        Pet pet = petRepository.findById(request.getPetId()).orElseThrow();
        Vet vet = vetRepository.findById(request.getVetId()).orElseThrow();

        Appointment appointment = new Appointment();
        appointment.setDate(request.getDate());
        appointment.setTime(request.getTime());
        appointment.setReason(request.getReason());
        appointment.setStatus("CONFIRMED");
        appointment.setOwner(owner);
        appointment.setPet(pet);
        appointment.setVet(vet);

        appointmentRepository.save(appointment);

        return ResponseEntity.ok(appointment);
    }

    @Operation(summary = "Get my appointments", description = "Returns all appointments for the authenticated owner")
    @ApiResponse(responseCode = "200", description = "List of owner appointments returned")
    @GetMapping("/my-appointments")
    public List<Appointment> getMyAppointments(Authentication authentication) {
        String email = authentication.getName();
        
        // ✅ FIXED: Assign to variable to silence "unused field" warning
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User login not found"));

        // Find Owner by EMAIL
        Owner owner = ownerRepository.findByEmail(email).orElseThrow();
        return appointmentRepository.findByOwnerId(owner.getId());
    }
    
    @Operation(summary = "Delete appointment", description = "Deletes an appointment by ID (Admin use)")
    @ApiResponse(responseCode = "200", description = "Appointment deleted")
    @DeleteMapping("/{id}")
    public void deleteAppointment(@PathVariable Long id) {
        appointmentRepository.deleteById(id);
    }

    @Operation(summary = "Get vet schedule", description = "Returns all appointments for the authenticated veterinarian")
    @ApiResponse(responseCode = "200", description = "Vet schedule returned")
    @GetMapping("/vet-schedule")
    public List<Appointment> getVetSchedule(Authentication authentication) {
        String email = authentication.getName();
        Vet vet = vetRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Vet profile not found"));
        
        return appointmentRepository.findByVetId(vet.getId());
    }
}
