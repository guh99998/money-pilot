package com.gustavolopes.money_pilot.repository;

import com.gustavolopes.money_pilot.model.Parceiro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParceiroRepository extends JpaRepository<Parceiro, Long> {
}
