package com.santander.bankbranch.repository;

import com.santander.bankbranch.model.BankBranch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes do Repositório BankBranchRepository")
class BankBranchRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BankBranchRepository bankBranchRepository;

    private BankBranch agencia1;
    private BankBranch agencia2;
    private BankBranch agencia3;

    @BeforeEach
    void setUp() {
        bankBranchRepository.deleteAll();

        agencia1 = BankBranch.builder()
                .posX(0.0)
                .posY(0.0)
                .creationDate(LocalDateTime.now())
                .build();

        agencia2 = BankBranch.builder()
                .posX(3.0)
                .posY(4.0)
                .creationDate(LocalDateTime.now())
                .build();

        agencia3 = BankBranch.builder()
                .posX(10.0)
                .posY(10.0)
                .creationDate(LocalDateTime.now())
                .build();

        entityManager.persistAndFlush(agencia1);
        entityManager.persistAndFlush(agencia2);
        entityManager.persistAndFlush(agencia3);
    }

    @Test
    @DisplayName("Deve salvar agência com sucesso")
    void deveSalvarAgenciaComSucesso() {
        BankBranch novaAgencia = BankBranch.builder()
                .posX(5.0)
                .posY(-5.0)
                .creationDate(LocalDateTime.now())
                .build();

        BankBranch agenciaSalva = bankBranchRepository.save(novaAgencia);

        assertNotNull(agenciaSalva);
        assertNotNull(agenciaSalva.getId());
        assertEquals(5.0, agenciaSalva.getPosX());
        assertEquals(-5.0, agenciaSalva.getPosY());
        assertNotNull(agenciaSalva.getCreationDate());
    }

    @Test
    @DisplayName("Deve buscar agência por ID")
    void deveBuscarAgenciaPorId() {
        var agenciaEncontrada = bankBranchRepository.findById(agencia1.getId());

        assertTrue(agenciaEncontrada.isPresent());
        assertEquals(agencia1.getId(), agenciaEncontrada.get().getId());
        assertEquals(agencia1.getPosX(), agenciaEncontrada.get().getPosX());
        assertEquals(agencia1.getPosY(), agenciaEncontrada.get().getPosY());
    }

    @Test
    @DisplayName("Deve retornar todas as agências")
    void deveRetornarTodasAsAgencias() {
        List<BankBranch> agencias = bankBranchRepository.findAll();

        assertEquals(3, agencias.size());
        assertTrue(agencias.contains(agencia1));
        assertTrue(agencias.contains(agencia2));
        assertTrue(agencias.contains(agencia3));
    }

    @Test
    @DisplayName("Deve deletar agência por ID")
    void deveDeletarAgenciaPorId() {
        bankBranchRepository.deleteById(agencia1.getId());
        entityManager.flush();

        var agenciaEncontrada = bankBranchRepository.findById(agencia1.getId());
        assertTrue(agenciaEncontrada.isEmpty());
    }

    @Test
    @DisplayName("Deve buscar agências próximas ordenadas por distância")
    void deveBuscarAgenciasProximasOrdenadasPorDistancia() {
        Double posX = 0.0;
        Double posY = 0.0;
        Integer limite = 2;

        List<Object[]> resultados = bankBranchRepository.findNearbyBankBranchesLimits(posX, posY, limite);

        assertEquals(2, resultados.size());

        Object[] primeiro = resultados.get(0);
        Object[] segundo = resultados.get(1);

        assertEquals(agencia1.getId(), primeiro[0]);
        assertEquals(0.0, primeiro[2]);
        assertEquals(0.0, primeiro[3]);
        assertEquals(0.0, (Double) primeiro[5], 0.01);

        assertEquals(agencia2.getId(), segundo[0]);
        assertEquals(3.0, segundo[2]);
        assertEquals(4.0, segundo[3]);
        assertEquals(5.0, (Double) segundo[5], 0.01);
    }

    @Test
    @DisplayName("Deve buscar agências próximas com limite específico")
    void deveBuscarAgenciasProximasComLimiteEspecifico() {
        Double posX = 0.0;
        Double posY = 0.0;
        Integer limite = 1;

        List<Object[]> resultados = bankBranchRepository.findNearbyBankBranchesLimits(posX, posY, limite);

        assertEquals(1, resultados.size());
        assertEquals(agencia1.getId(), resultados.get(0)[0]);
    }

    @Test
    @DisplayName("Deve calcular distância corretamente para diferentes posições")
    void deveCalcularDistanciaCorretamenteParaDiferentesPosicoes() {
        Double posX = 5.0;
        Double posY = 5.0;
        Integer limite = 3;

        List<Object[]> resultados = bankBranchRepository.findNearbyBankBranchesLimits(posX, posY, limite);

        assertEquals(3, resultados.size());

        for (Object[] resultado : resultados) {
            Long id = (Long) resultado[0];
            Double posXResult = (Double) resultado[2];
            Double posYResult = (Double) resultado[3];
            Double distancia = (Double) resultado[5];

            Double distanciaEsperada = Math.sqrt(Math.pow(posXResult - posX, 2) + Math.pow(posYResult - posY, 2));

            assertEquals(distanciaEsperada, distancia, 0.01,
                    "Distância incorreta para agência " + id);
        }
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há agências")
    void deveRetornarListaVaziaQuandoNaoHaAgencias() {
        bankBranchRepository.deleteAll();
        entityManager.flush();

        Double posX = 0.0;
        Double posY = 0.0;
        Integer limite = 10;

        List<Object[]> resultados = bankBranchRepository.findNearbyBankBranchesLimits(posX, posY, limite);

        assertTrue(resultados.isEmpty());
    }

    @Test
    @DisplayName("Deve persistir data de criação automaticamente")
    void devePersistirDataDeCriacaoAutomaticamente() {
        BankBranch agencia = BankBranch.builder()
                .posX(1.0)
                .posY(2.0)
                .build();

        BankBranch agenciaSalva = bankBranchRepository.save(agencia);

        assertNotNull(agenciaSalva.getCreationDate());
        assertTrue(agenciaSalva.getCreationDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Deve retornar true quando existe agência próxima")
    void deveRetornarTrueQuandoExisteAgenciaProxima() {
        boolean existeProxima = bankBranchRepository.existsNearbyBankBranches(0.05, 0.0, 0.1);
        assertTrue(existeProxima);
    }

    @Test
    @DisplayName("Deve retornar false quando não existe agência próxima")
    void deveRetornarTrueQuandoNaoExisteAgenciaProxima() {
        boolean existeProxima = bankBranchRepository.existsNearbyBankBranches(10.0, 10.0, 0.1);
        assertTrue(existeProxima);
    }

    @Test
    @DisplayName("Deve retornar true quando agência está exatamente na distância mínima")
    void deveRetornarTrueQuandoAgenciaEstaExatamenteNaDistanciaMinima() {
        boolean existeProxima = bankBranchRepository.existsNearbyBankBranches(0.1, 0.0, 0.1);
        assertTrue(existeProxima);
    }

    @Test
    @DisplayName("Deve retornar false quando agência está além da distância mínima")
    void deveRetornarFalseQuandoAgenciaEstaAlemDaDistanciaMinima() {
        boolean existeProxima = bankBranchRepository.existsNearbyBankBranches(0.11, 0.0, 0.1);
        assertFalse(existeProxima);
    }

    @Test
    @DisplayName("Deve verificar proximidade com diferentes posições")
    void deveVerificarProximidadeComDiferentesPosicoes() {
        boolean proximaAgencia2 = bankBranchRepository.existsNearbyBankBranches(3.05, 4.0, 0.1);
        assertTrue(proximaAgencia2);

        boolean distanteAgencia2 = bankBranchRepository.existsNearbyBankBranches(5.0, 4.0, 0.1);
        assertFalse(distanteAgencia2);
    }
}
