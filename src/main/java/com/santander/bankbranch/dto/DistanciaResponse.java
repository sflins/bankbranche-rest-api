package com.santander.bankbranch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record DistanciaResponse(
    
    @JsonProperty("posicaoUsuario")
    PosicaoUsuario posicaoUsuario,

    @JsonProperty("agencias")
    Map<String, String> agencias,

    @JsonProperty("totalAgencias")
    Integer totalAgencias,

    @JsonProperty("agenciaMaisProxima")
    String agenciaMaisProxima,

    @JsonProperty("menorDistancia")
    Double menorDistancia
) {

    public record PosicaoUsuario(
        
        @JsonProperty("posX")
        Double posX,

        @JsonProperty("posY")
        Double posY
    ) {}
}
