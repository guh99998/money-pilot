package com.gustavolopes.money_pilot.repository;

import com.gustavolopes.money_pilot.model.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BancoRepository extends JpaRepository<Banco, Long> {
}
