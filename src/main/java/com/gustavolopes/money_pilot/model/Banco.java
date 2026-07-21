package com.gustavolopes.money_pilot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Banco {
    @Id
    private long id;
    private String codigoBanco;
    private String nomeBanco;
}
