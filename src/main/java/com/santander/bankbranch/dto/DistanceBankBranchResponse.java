package com.santander.bankbranch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record DistanceBankBranchResponse(

    @JsonProperty("agencias")
    Map<String, String> agencias
)
{}
