package com.gustavolopes.money_pilot.repository;

import com.gustavolopes.money_pilot.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}
