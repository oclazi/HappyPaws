package com.happypaws.petclinic.controller; // ✅ Fixed Package

import com.happypaws.petclinic.dto.DiseasePredictionRequest; // ✅ Fixed Import
import com.happypaws.petclinic.dto.DiseasePredictionResponse; // ✅ Fixed Import
import com.happypaws.petclinic.service.ChatbotService; // ✅ Fixed Import

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "http://localhost:3000") // Allow React Frontend
@Tag(name = "Chatbot", description = "AI-powered disease prediction chatbot for pets")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @Operation(summary = "Predict disease", description = "Analyzes pet symptoms and predicts possible diseases using AI")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Prediction returned successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/predict")
    public ResponseEntity<DiseasePredictionResponse> predictDisease(@RequestBody DiseasePredictionRequest request) {
        DiseasePredictionResponse response = chatbotService.predictDisease(request);
        return ResponseEntity.ok(response);
    }
}
