package com.gustavolopes.money_pilot.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("PJ")
@Table(name = "parceiro_pj")
@PrimaryKeyJoinColumn(name = "parceiro_id")
public class ParceiroPJ extends Parceiro {

    @NotBlank
    @Pattern(regexp = "^\\d{14}$")
    String cnpj;
}
