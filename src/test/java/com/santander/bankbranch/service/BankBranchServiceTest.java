package com.santander.bankbranch.service;

import com.santander.bankbranch.dto.RegisterBankBranchRequest;
import com.santander.bankbranch.dto.RegisterBankBranchResponse;
import com.santander.bankbranch.dto.DistanciaResponse;
import com.santander.bankbranch.model.BankBranch;
import com.santander.bankbranch.repository.BankBranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("BankBranchService Service Tests")
class BankBranchServiceTest {

    @Mock
    private BankBranchRepository bankBranchRepository;

    @InjectMocks
    private BankBranchService bankBranchService;

    private BankBranch agencia;
    private RegisterBankBranchRequest request;

    private static final Double DISTANCIA_MINIMA_ENTRE_AGENCIAS = 0.1;

    @BeforeEach
    void setUp() {
        agencia = BankBranch.builder()
                .id(1L)
                .name("AGENCIA_1")
                .posX(10.0)
                .posY(-5.0)
                .creationDate(LocalDateTime.now())
                .build();

        request = new RegisterBankBranchRequest(10.0, -5.0);
    }

    @Test
    @DisplayName("Should register branch successfully")
    void deveCadastrarAgenciaComSucesso() {
        when(bankBranchRepository.existsNearbyBankBranches(10.0, -5.0, 0.1)).thenReturn(false);
        when(bankBranchRepository.countComLock()).thenReturn(0L);
        when(bankBranchRepository.save(any(BankBranch.class))).thenReturn(agencia);

        RegisterBankBranchResponse response = bankBranchService.registerBankBranch(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("AGENCIA_1", response.nome());
        assertEquals(10.0, response.posX());
        assertEquals(-5.0, response.posY());
        assertEquals("Branch registered successfully!", response.mensagem());

        verify(bankBranchRepository).existsNearbyBankBranches(10.0, -5.0, 0.1);
        verify(bankBranchRepository).countComLock();
        verify(bankBranchRepository).save(any(BankBranch.class));
    }

    @Test
    @DisplayName("Should register branches with valid locations")
    void deveCadastrarAgenciaComPosicoesValidas() {
        request = new RegisterBankBranchRequest(15.0, -10.0);
        
        BankBranch agenciaEsperada = BankBranch.builder()
                .id(1L)
                .name("AGENCIA_1")
                .posX(15.0)
                .posY(-10.0)
                .creationDate(LocalDateTime.now())
                .build();
        
        when(bankBranchRepository.existsNearbyBankBranches(15.0, -10.0, 0.1)).thenReturn(false);
        when(bankBranchRepository.countComLock()).thenReturn(0L);
        when(bankBranchRepository.save(any(BankBranch.class))).thenReturn(agenciaEsperada);

        RegisterBankBranchResponse response = bankBranchService.registerBankBranch(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("AGENCIA_1", response.nome());
        assertEquals(15.0, response.posX());
        assertEquals(-10.0, response.posY());

        verify(bankBranchRepository).existsNearbyBankBranches(15.0, -10.0, 0.1);
        verify(bankBranchRepository).countComLock();
        verify(bankBranchRepository).save(any(BankBranch.class));
    }

    @Test
    @DisplayName("Should throw exception when locations are invalid")
    void deveLancarExcecaoQuandoPosicoesSaoInvalidas() {
        RegisterBankBranchRequest requestInvalido = new RegisterBankBranchRequest(null, -5.0);

        assertThrows(
                IllegalArgumentException.class,
                () -> bankBranchService.registerBankBranch(requestInvalido)
        );

        verify(bankBranchRepository, never()).save(any(BankBranch.class));
    }

    @Test
    @DisplayName("Should search for nearby branches successfully")
    void deveBuscarAgenciasProximasComSucesso() {
        Object[] resultado1 = {1L, "AGENCIA_1", 0.0, 0.0, java.sql.Timestamp.valueOf(LocalDateTime.now()), 5.0};
        Object[] resultado2 = {2L, "AGENCIA_2", 5.0, 5.0, java.sql.Timestamp.valueOf(LocalDateTime.now()), 7.07};

        when(bankBranchRepository.findNearbyBankBranchesLimits(0.0, 0.0, 1000))
                .thenReturn(Arrays.asList(resultado1, resultado2));

        DistanciaResponse response = bankBranchService.findNearbyBankBranches(0.0, 0.0);

        assertNotNull(response);
        assertEquals(2, response.totalAgencias());
        assertEquals("AGENCIA_1", response.agenciaMaisProxima());
        assertEquals(5.0, response.menorDistancia(), 0.01);
        assertTrue(response.agencias().containsKey("AGENCIA_1"));
        assertTrue(response.agencias().containsKey("AGENCIA_2"));
        assertTrue(response.agencias().get("AGENCIA_1").contains("5.00"));
        assertTrue(response.agencias().get("AGENCIA_2").contains("7.07"));

        verify(bankBranchRepository).findNearbyBankBranchesLimits(0.0, 0.0, 1000);
    }


    @Test
    @DisplayName("Must throw an exception when attempting to register a branch too close")
    void deveLancarExcecaoQuandoTentarCadastrarAgenciaMuitoProxima() {
        when(bankBranchRepository.existsNearbyBankBranches(10.0, -5.0, 0.1)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankBranchService.registerBankBranch(request)
        );

        String expectedMessage = String.format("There is already a branch close to this position. Minimum distance allowed: %.1f units", DISTANCIA_MINIMA_ENTRE_AGENCIAS);

        System.out.println("exception.getMessage() " + exception.getMessage());
        assertEquals(expectedMessage, exception.getMessage());
        verify(bankBranchRepository).existsNearbyBankBranches(10.0, -5.0, 0.1);
        verify(bankBranchRepository, never()).save(any(BankBranch.class));
    }

    @Test
    @DisplayName("Must allow registration when there is no nearby branch")
    void devePermitirCadastroQuandoNaoHaAgenciaProxima() {
        when(bankBranchRepository.existsNearbyBankBranches(10.0, -5.0, 0.1)).thenReturn(false);
        when(bankBranchRepository.countComLock()).thenReturn(0L);
        when(bankBranchRepository.save(any(BankBranch.class))).thenReturn(agencia);

        RegisterBankBranchResponse response = bankBranchService.registerBankBranch(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("AGENCIA_1", response.nome());
        verify(bankBranchRepository).existsNearbyBankBranches(10.0, -5.0, 0.1);
        verify(bankBranchRepository).save(any(BankBranch.class));
    }

    @Test
    @DisplayName("Must allow registration when the branch is within the exact minimum distance")
    void devePermitirCadastroQuandoAgenciaEstaNaDistanciaMinimaExata() {
        RegisterBankBranchRequest requestDistanciaExata = new RegisterBankBranchRequest(10.1, -5.0);
        when(bankBranchRepository.existsNearbyBankBranches(10.1, -5.0, 0.1)).thenReturn(false);
        when(bankBranchRepository.countComLock()).thenReturn(0L);
        when(bankBranchRepository.save(any(BankBranch.class))).thenReturn(agencia);

        RegisterBankBranchResponse response = bankBranchService.registerBankBranch(requestDistanciaExata);

        assertNotNull(response);
        verify(bankBranchRepository).existsNearbyBankBranches(10.1, -5.0, 0.1);
        verify(bankBranchRepository).save(any(BankBranch.class));
    }
}
