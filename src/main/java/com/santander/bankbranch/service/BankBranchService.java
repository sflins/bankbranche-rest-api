package com.santander.bankbranch.service;

import com.santander.bankbranch.dto.RegisterBankBranchRequest;
import com.santander.bankbranch.dto.RegisterBankBranchResponse;
import com.santander.bankbranch.dto.DistanceBankBranchResponse;
import com.santander.bankbranch.dto.DistanciaResponse;
import com.santander.bankbranch.model.BankBranch;
import com.santander.bankbranch.repository.BankBranchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BankBranchService {

    private static final Logger logger = LoggerFactory.getLogger(BankBranchService.class);
    private static final Double DISTANCIA_MINIMA_ENTRE_AGENCIAS = 0.1;

    @Autowired
    private BankBranchRepository bankBranchRepository;

    @Transactional
    public RegisterBankBranchResponse registerBankBranch(RegisterBankBranchRequest request) {
        if (request == null || request.posX() == null || request.posY() == null) {
            throw new IllegalArgumentException("Parâmetros posX e posY são obrigatórios");
        }

        logger.info("Starting branch registration at position ({}, {})", request.posX(), request.posY());

        if (bankBranchRepository.existsNearbyBankBranches(request.posX(), request.posY(), DISTANCIA_MINIMA_ENTRE_AGENCIAS)) {
            logger.warn("Attempting to register a branch very close to an existing one at position ({}, {})",
                    request.posX(), request.posY());
            throw new IllegalArgumentException(
                    //String.format("Já existe uma agência próxima a esta posição. Distância mínima permitida: %.1f unidades", DISTANCIA_MINIMA_ENTRE_AGENCIAS)
                    String.format("There is already a branch close to this position. Minimum distance allowed: %.1f units", DISTANCIA_MINIMA_ENTRE_AGENCIAS)
            );
        }

        long proximoId = bankBranchRepository.countComLock() + 1;
        String nomeAgencia = "AGENCIA_" + proximoId;

        BankBranch bankBranch = BankBranch.builder()
                .name(nomeAgencia)
                .posX(request.posX())
                .posY(request.posY())
                .build();
        bankBranch = bankBranchRepository.save(bankBranch);

        logger.info("Branch successfully registered - ID: {}, Nome: {}", bankBranch.getId(), bankBranch.getName());

        return new RegisterBankBranchResponse(
                bankBranch.getId(),
                bankBranch.getName(),
                bankBranch.getPosX(),
                bankBranch.getPosY(),
                bankBranch.getCreationDate(),
                "Branch registered successfully!"
        );
    }

    @Transactional(readOnly = true)
    public DistanciaResponse findNearbyBankBranches(Double posX, Double posY) {
        if (posX == null || posY == null) {
            throw new IllegalArgumentException("Parâmetros posX e posY são obrigatórios");
        }

        logger.info("Searching for branches near the position ({}, {})", posX, posY);

        try {
            List<Object[]> resultados = bankBranchRepository.findNearbyBankBranchesLimits(
                    posX, posY, 1000
            );

            DistanciaResponse response = processarResultadosAgencias(resultados, posX, posY);

            logger.info("Found {} branches near the position ({}, {})",
                    response.totalAgencias(), posX, posY);

            return response;

        } catch (Exception e) {
            logger.error("Error searching for nearby branches: {}", e.getMessage(), e);
            throw new RuntimeException("Internal error searching for nearby branches", e);
        }
    }

    @Transactional(readOnly = true)
    public DistanceBankBranchResponse findBankBranches(Double posX, Double posY) {
        if (posX == null || posY == null) {
            throw new IllegalArgumentException("Parameters posX and posY are mandatory");
        }

        logger.info("Searching for branches at the position ({}, {})", posX, posY);

        try {
            List<Object[]> resultados = bankBranchRepository.findNearbyBankBranches(
                    posX, posY
            );

            DistanceBankBranchResponse response = processarResultadosAgenciasSimples(resultados, posX, posY);



            return response;

        } catch (Exception e) {
            logger.error("Error searching for nearby branches: {}", e.getMessage(), e);
            throw new RuntimeException("Internal error searching for nearby branches", e);
        }
    }


    private DistanciaResponse processarResultadosAgencias(List<Object[]> resultados, Double posX, Double posY) {
        Map<String, String> agencias = new LinkedHashMap<>();
        String agenciaMaisProxima = null;
        Double menorDistancia = null;

        for (Object[] resultado : resultados) {
            BankBranch agencia = construirAgencia(resultado);
            Double distancia = ((Number) resultado[5]).doubleValue();

            String distanciaFormatada = formatarDistancia(distancia);
            String nomeAgencia = agencia.getName();
            agencias.put(nomeAgencia, distanciaFormatada);

            if (agenciaMaisProxima == null || distancia < menorDistancia) {
                agenciaMaisProxima = nomeAgencia;
                menorDistancia = distancia;
            }
        }

        return new DistanciaResponse(
                new DistanciaResponse.PosicaoUsuario(posX, posY),
                agencias,
                agencias.size(),
                agenciaMaisProxima,
                menorDistancia
        );
    }

    private DistanceBankBranchResponse processarResultadosAgenciasSimples(List<Object[]> resultados, Double posX, Double posY) {
        Map<String, String> agencias = new LinkedHashMap<>();
        String agenciaMaisProxima = null;
        Double menorDistancia = null;

        for (Object[] resultado : resultados) {
            BankBranch agencia = construirAgencia(resultado);
            Double distancia = ((Number) resultado[5]).doubleValue();

            String distanciaFormatada = formatarDistancia(distancia);
            String nomeAgencia = agencia.getName();
            agencias.put(nomeAgencia, distanciaFormatada);

            if (agenciaMaisProxima == null || distancia < menorDistancia) {
                agenciaMaisProxima = nomeAgencia;
                menorDistancia = distancia;
            }
        }

        return new DistanceBankBranchResponse(
                agencias
        );
    }

    private BankBranch construirAgencia(Object[] resultado) {
        return BankBranch.builder()
                .id(((Number) resultado[0]).longValue())
                .name((String) resultado[1])
                .posX(((Number) resultado[2]).doubleValue())
                .posY(((Number) resultado[3]).doubleValue())
                .creationDate(((java.sql.Timestamp) resultado[4]).toLocalDateTime())
                .build();
    }

    private String formatarDistancia(Double distancia) {
        return String.format("distancia = %.2f", distancia).replace(",", ".");
    }
}
