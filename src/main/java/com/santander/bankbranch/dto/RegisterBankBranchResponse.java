package com.santander.bankbranch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record RegisterBankBranchResponse(
    
    @JsonProperty("id")
    Long id,

    @JsonProperty("nome")
    String nome,

    @JsonProperty("posX")
    Double posX,

    @JsonProperty("posY")
    Double posY,

    @JsonProperty("dataCriacao")
    LocalDateTime dataCriacao,

    @JsonProperty("mensagem")
    String mensagem
) {
    public RegisterBankBranchResponse(Long id, String nome, Double posX, Double posY, LocalDateTime dataCriacao) {
        this(id, nome, posX, posY, dataCriacao, "Agência cadastrada com sucesso!");
    }
}
