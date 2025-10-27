package com.santander.bankbranch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santander.bankbranch.dto.RegisterBankBranchRequest;
import com.santander.bankbranch.dto.RegisterBankBranchResponse;
import com.santander.bankbranch.dto.DistanciaResponse;
import com.santander.bankbranch.service.BankBranchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BankBranchController Controller Tests")
class BankBranchControllerTest {

    @Mock
    private BankBranchService bankBranchService;

    @InjectMocks
    private BankBranchController bankBranchController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bankBranchController)
                .setControllerAdvice(new com.santander.bankbranch.exception.GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should successfully register branch")
    void deveCadastrarAgenciaComSucesso() throws Exception {
        RegisterBankBranchRequest request = new RegisterBankBranchRequest(10.0, -5.0);
        RegisterBankBranchResponse response = new RegisterBankBranchResponse(
                1L,
                "AGENCIA_1",
                10.0,
                -5.0,
                LocalDateTime.now(),
                "Branch successfully registered!"
        );

        when(bankBranchService.registerBankBranch(any(RegisterBankBranchRequest.class))).thenReturn(response);

        mockMvc.perform(post("/desafio/cadastrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("AGENCIA_1"))
                .andExpect(jsonPath("$.posX").value(10.0))
                .andExpect(jsonPath("$.posY").value(-5.0))
                .andExpect(jsonPath("$.mensagem").value("Branch successfully registered!"));

        verify(bankBranchService).registerBankBranch(any(RegisterBankBranchRequest.class));
    }

    @Test
    @DisplayName("Should return error 400 for invalid data")
    void deveRetornarErro400ParaDadosInvalidos() throws Exception {
        RegisterBankBranchRequest request = new RegisterBankBranchRequest(null, null); // posX e posY null

        mockMvc.perform(post("/desafio/cadastrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(bankBranchService, never()).registerBankBranch(any());
    }

    @Test
    @DisplayName("Should return a 400 error when trying to register a very close branch")
    void deveRetornarErro400QuandoTentarCadastrarAgenciaMuitoProxima() throws Exception {
        RegisterBankBranchRequest request = new RegisterBankBranchRequest(10.0, -5.0);
        
        when(bankBranchService.registerBankBranch(any(RegisterBankBranchRequest.class)))
            .thenThrow(new IllegalArgumentException("There is already a branch near this location. Minimum distance allowed: 0.1 units"));

        mockMvc.perform(post("/desafio/cadastrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(bankBranchService).registerBankBranch(any(RegisterBankBranchRequest.class));
    }
}
