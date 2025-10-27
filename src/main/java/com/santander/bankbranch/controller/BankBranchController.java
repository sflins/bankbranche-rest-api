package com.santander.bankbranch.controller;

import com.santander.bankbranch.dto.RegisterBankBranchRequest;
import com.santander.bankbranch.dto.RegisterBankBranchResponse;
import com.santander.bankbranch.dto.DistanceBankBranchResponse;
import com.santander.bankbranch.service.BankBranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/desafio")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"}, allowCredentials = "true")
@Validated
@Tag(name = "Bank Branches", description = "API for registering and finding bank branches by coordinates")
public class BankBranchController {

    @Autowired
    private BankBranchService bankBranchService;

    @PostMapping("/cadastrar")
    @Operation(summary = "Register a new bank branch", description = "Creates a bank branch with given coordinates (posX, posY)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Bank branch registered successfully",
                    content = @Content(schema = @Schema(implementation = RegisterBankBranchResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid coordinates or request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    })
    public ResponseEntity<RegisterBankBranchResponse> bankBranchRegister(
            @Valid @RequestBody @Parameter(description = "Request body with branch coordinates", required = true)
            RegisterBankBranchRequest request) {
        try {
            RegisterBankBranchResponse response = bankBranchService.registerBankBranch(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            // Use the full constructor with default/null values for error response
            RegisterBankBranchResponse errorResponse = new RegisterBankBranchResponse(
                    null, // id
                    null, // nome
                    request.posX(), // posX
                    request.posY(), // posY
                    null, // dataCriacao
                    "Error: " + e.getMessage() // mensagem
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/distancia")
    @Operation(summary = "Find bank branches near coordinates", description = "Returns a list of bank branches near the specified posX and posY coordinates")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of nearby branches",
                    content = @Content(schema = @Schema(implementation = DistanceBankBranchResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid coordinates"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    })
    public ResponseEntity<DistanceBankBranchResponse> findBankBranches(
            @Parameter(description = "X coordinate (longitude, -180 to 180)", required = true)
            @RequestParam @NotNull(message = "Position X is mandatory")
            @DecimalMin(value = "-180.0", message = "Position X must be greater than or equal to -180")
            @DecimalMax(value = "180.0", message = "Position X must be less than or equal to 180")
            Double posX,

            @Parameter(description = "Y coordinate (latitude, -90 to 90)", required = true)
            @RequestParam @NotNull(message = "Position Y is mandatory")
            @DecimalMin(value = "-90.0", message = "Position Y must be greater than or equal to -90")
            @DecimalMax(value = "90.0", message = "Position Y must be less than or equal to 90")
            Double posY) {
        try {
            DistanceBankBranchResponse response = bankBranchService.findBankBranches(posX, posY);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Return a DistanceBankBranchResponse with an error message in the agencias map
            DistanceBankBranchResponse errorResponse = new DistanceBankBranchResponse(
                    Collections.singletonMap("error", e.getMessage())
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}