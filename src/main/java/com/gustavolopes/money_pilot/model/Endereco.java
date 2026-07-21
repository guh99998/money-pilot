package com.gustavolopes.money_pilot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Endereco {
  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private String rua;

  @NotNull
  private String numero;

  @NotNull
  private String bairro;

  @NotBlank
  private String cep;

  @NotBlank
  private String cidade;

  @NotBlank
  private String uf;
}
