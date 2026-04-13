package com.happypaws.petclinic.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.happypaws.petclinic.entity.Visit;
import com.happypaws.petclinic.service.VisitService;

@RestController
@RequestMapping("/api/visits")
@CrossOrigin(origins = "*")
@Tag(name = "Visits", description = "Endpoints for managing veterinary visits")
public class VisitController {

    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @Operation(summary = "Create a visit", description = "Records a new veterinary visit")
    @ApiResponse(responseCode = "200", description = "Visit created successfully")
    @PostMapping
    public Visit createVisit(@RequestBody Visit visit) {
        return visitService.createVisit(visit);
    }

    @Operation(summary = "Get all visits", description = "Returns a list of all veterinary visits")
    @ApiResponse(responseCode = "200", description = "List of visits returned")
    @GetMapping
    public List<Visit> getAllVisits() {
        return visitService.getAllVisits();
    }

    @Operation(summary = "Get visit by ID", description = "Returns a specific visit by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Visit found"),
        @ApiResponse(responseCode = "404", description = "Visit not found")
    })
    @GetMapping("/{id}")
    public Visit getVisitById(@PathVariable Long id) {
        return visitService.getVisitById(id);
    }

    @Operation(summary = "Update visit", description = "Updates an existing visit record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Visit updated successfully"),
        @ApiResponse(responseCode = "404", description = "Visit not found")
    })
    @PutMapping("/{id}")
    public Visit updateVisit(@PathVariable Long id, @RequestBody Visit visit) {
        return visitService.updateVisit(id, visit);
    }

    @Operation(summary = "Delete visit", description = "Deletes a visit by its ID")
    @ApiResponse(responseCode = "200", description = "Visit deleted successfully")
    @DeleteMapping("/{id}")
    public String deleteVisit(@PathVariable Long id) {
        visitService.deleteVisit(id);
        return "Visit deleted successfully with id: " + id;
    }
    
    @Operation(summary = "Bulk insert visits", description = "Creates multiple visit records in a single request")
    @ApiResponse(responseCode = "200", description = "Visits created successfully")
    @PostMapping("/bulk")
    public List<Visit> createVisits(@RequestBody List<Visit> visits) {
        return visitService.createVisits(visits);
    }

}
