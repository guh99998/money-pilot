package com.gustavolopes.money_pilot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Parceiro {
  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  private String nome;

  @NotNull
  private String documento;

  @NotNull
  private String telefone;

  @NotNull
  private String email;

  @NotNull
  @Enumerated(EnumType.STRING)
  @ElementCollection
  private Set<TipoParceiro> tipoParceiros = new HashSet<>();

  @NotNull
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "endereco_id")
  private Endereco endereco;
}
