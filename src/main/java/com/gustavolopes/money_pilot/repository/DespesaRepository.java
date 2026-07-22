package com.gustavolopes.money_pilot.repository;

import com.gustavolopes.money_pilot.model.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {
}
