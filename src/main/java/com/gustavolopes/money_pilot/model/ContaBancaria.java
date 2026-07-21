package com.gustavolopes.money_pilot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ContaBancaria {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "banco_id")
    private Banco banco;

    @NotBlank
    private String nomeContaBancaria;

    @NotBlank
    private String agencia;

    @NotBlank
    private String numeroConta;

    @NotNull
    private BigDecimal saldoInicial;
}
