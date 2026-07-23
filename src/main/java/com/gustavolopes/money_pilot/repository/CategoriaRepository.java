package com.gustavolopes.money_pilot.repository;

import com.gustavolopes.money_pilot.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Long id(Long id);
}
