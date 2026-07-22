package com.gustavolopes.money_pilot.repository;

import com.gustavolopes.money_pilot.model.Receita;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceitaRepository extends JpaRepository<Receita, Long> {
}
