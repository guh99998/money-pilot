package com.gustavolopes.money_pilot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@ToString
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Despesa {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    private String nomeDespesa;

    @ManyToOne
    @JoinColumn(name = "parceiro_id")
    private Parceiro parceiro;

    @NotNull
    private LocalDate dataVencimento;

    @NotNull
    private int numeroParcelas;

    @NotNull
    private BigDecimal valorParcelas;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @NotNull
    private String observacoes;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "pagamento_id")
    private Pagamento pagamento;
}
