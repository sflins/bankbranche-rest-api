package com.santander.bankbranch.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "bankbranch", indexes = {
    @Index(name = "idx_position", columnList = "pos_x, pos_y"),
    @Index(name = "idx_creation_date", columnList = "creation_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pos_x", nullable = false)
    @NotNull(message = "Posição X é obrigatória")
    private Double posX;

    @Column(name = "pos_y", nullable = false)
    @NotNull(message = "Posição Y é obrigatória")
    private Double posY;

    @Column(name = "name", nullable = true, length = 100)
    private String name;

    @Column(name = "creation_date", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
}
