package com.gustavolopes.money_pilot.repository;

import com.gustavolopes.money_pilot.model.ContaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, Long> {
}
