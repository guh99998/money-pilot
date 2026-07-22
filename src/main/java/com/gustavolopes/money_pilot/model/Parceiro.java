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
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_documento")
public abstract class Parceiro {
  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  private String nome;

  @NotNull
  private String telefone;

  @NotNull
  private String email;

  @ElementCollection
  @Enumerated(EnumType.STRING)
  @CollectionTable(name = "parceiro_tipo_parceiro", joinColumns = @JoinColumn(name = "parceiro_id"))
  @Column(name = "tipo_parceiro")
  private Set<TipoParceiro> tipoParceiros = new HashSet<>();

  @NotNull
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "endereco_id")
  private Endereco endereco;
}
