package com.santander.bankbranch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record RegisterBankBranchRequest(
    
    @JsonProperty("posX")
    @NotNull(message = "Position X is mandatory")
    @DecimalMin(value = "-180.0", message = "Position X must be greater than or equal to -180")
    @DecimalMax(value = "180.0", message = "Position X must be less than or equal to 180")
    Double posX,

    @JsonProperty("posY")
    @NotNull(message = "Position Y is mandatory")
    @DecimalMin(value = "-90.0", message = "Position Y must be greater than or equal to -180")
    @DecimalMax(value = "90.0", message = "Position Y must be less than or equal to 180")
    Double posY
) {}
