package com.santander.bankbranch.controller;

import com.santander.bankbranch.dto.RegisterBankBranchRequest;
import com.santander.bankbranch.dto.RegisterBankBranchResponse;
import com.santander.bankbranch.dto.DistanceBankBranchResponse;
import com.santander.bankbranch.dto.DistanciaResponse;
import com.santander.bankbranch.service.BankBranchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/desafio")
@CrossOrigin(origins = "*")
@Validated
public class BankBranchController {


    @Autowired
    private BankBranchService bankBranchService;

    @PostMapping("/cadastrar")
    public ResponseEntity<RegisterBankBranchResponse> bankBranchRegister(
            @Valid @RequestBody RegisterBankBranchRequest request) {
        
        RegisterBankBranchResponse response = bankBranchService.registerBankBranch(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/distanciasproximas")
    public ResponseEntity<DistanciaResponse> findNearbyBankBranches(
            @RequestParam(value = "posX", required = true) 
            @NotNull(message = "Position X is mandatory")
            @DecimalMin(value = "-180.0", message = "Position X must be greater than or equal to -180")
            @DecimalMax(value = "180.0", message = "Position X must be less than or equal to 180")
            Double posX,
            
            @RequestParam(value = "posY", required = true) 
            @NotNull(message = "Position Y is mandatory")
            @DecimalMin(value = "-90.0", message = "Position X must be greater than or equal to -90")
            @DecimalMax(value = "90.0", message = "Position X must be less than or equal to 90")
            Double posY) {
        

        DistanciaResponse response = bankBranchService.findNearbyBankBranches(posX, posY);
        
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/distancia")
    public ResponseEntity<DistanceBankBranchResponse> findBankBranches(
            @NotNull(message = "Position X is mandatory")
            @DecimalMin(value = "-180.0", message = "Position X must be greater than or equal to -180")
            @DecimalMax(value = "180.0", message = "Position X must be less than or equal to 180")
            Double posX,

            @RequestParam(value = "posY", required = true)
            @NotNull(message = "Position Y is mandatory")
            @DecimalMin(value = "-90.0", message = "Position X must be greater than or equal to -90")
            @DecimalMax(value = "90.0", message = "Position X must be less than or equal to 90")
            Double posY) {


        DistanceBankBranchResponse response = bankBranchService.findBankBranches(posX, posY);


        return ResponseEntity.ok(response);
    }


}
