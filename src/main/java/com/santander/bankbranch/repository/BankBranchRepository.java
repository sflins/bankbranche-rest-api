package com.santander.bankbranch.repository;

import com.santander.bankbranch.model.BankBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

import java.util.List;


@Repository
public interface BankBranchRepository extends JpaRepository<BankBranch, Long> {

    @Query(value = """
        SELECT a.id, a.name, a.pos_x, a.pos_y, a.creation_date,
               SQRT(POWER(a.pos_x - :posX, 2) + POWER(a.pos_y - :posY, 2)) as distancia
        FROM bankbranch a 
        ORDER BY distancia ASC 
        LIMIT :limite
        """, nativeQuery = true)
    List<Object[]> findNearbyBankBranchesLimits(@Param("posX") Double posX,
                                                      @Param("posY") Double posY,
                                                      @Param("limite") Integer limite);

    @Query(value = """
        SELECT a.id, a.name, a.pos_x, a.pos_y, a.creation_date,
               SQRT(POWER(a.pos_x - :posX, 2) + POWER(a.pos_y - :posY, 2)) as distancia
        FROM bankbranch a 
        ORDER BY distancia ASC 
        """, nativeQuery = true)
    List<Object[]> findNearbyBankBranches(@Param("posX") Double posX,
                                          @Param("posY") Double posY);

    @Query(value = """
        SELECT COUNT(*) > 0
        FROM bankbranch a 
        WHERE SQRT(POWER(a.pos_x - :posX, 2) + POWER(a.pos_y - :posY, 2)) <= :distanciaMinima
        """, nativeQuery = true)
    boolean existsNearbyBankBranches(@Param("posX") Double posX,
                                 @Param("posY") Double posY,
                                 @Param("distanciaMinima") Double distanciaMinima);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT COUNT(a) FROM BankBranch a")
    long countComLock();

}
