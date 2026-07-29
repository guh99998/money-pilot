package com.gustavolopes.money_pilot.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class RecebimentoRequestDTOTest {

    static ValidatorFactory factory;
    static Validator validator;

    @BeforeAll
    public static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public static void tearDown() {
        factory.close();
    }

    private RecebimentoRequestDTO dtoValido() {
        return new RecebimentoRequestDTO(List.of(1L, 2L), LocalDate.now(), BigDecimal.valueOf(1000), 1L);
    }

    @Test
    public void naoDeveGerarViolacoesQuandoTodosOsCamposSaoValidos() {
        Set<ConstraintViolation<RecebimentoRequestDTO>> violacoes = validator.validate(dtoValido());

        assertThat(violacoes).isEmpty();
    }

    @Test
    public void deveGerarViolacaoQuandoReceitasIdEhNula() {
        RecebimentoRequestDTO dto = new RecebimentoRequestDTO(null, LocalDate.now(), BigDecimal.valueOf(1000), 1L);

        Set<ConstraintViolation<RecebimentoRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("Ao mínimo 1 receita é necessária");
    }

    @Test
    public void deveGerarViolacaoQuandoReceitasIdEstaVazia() {
        RecebimentoRequestDTO dto = new RecebimentoRequestDTO(List.of(), LocalDate.now(), BigDecimal.valueOf(1000), 1L);

        Set<ConstraintViolation<RecebimentoRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("receitasId");
    }

    @Test
    public void deveGerarViolacaoQuandoDataRecebimentoEhNula() {
        RecebimentoRequestDTO dto = new RecebimentoRequestDTO(List.of(1L), null, BigDecimal.valueOf(1000), 1L);

        Set<ConstraintViolation<RecebimentoRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("A data de recebimento é obrigatória");
    }

    @Test
    public void deveGerarViolacaoQuandoValorFinalEhNulo() {
        RecebimentoRequestDTO dto = new RecebimentoRequestDTO(List.of(1L), LocalDate.now(), null, 1L);

        Set<ConstraintViolation<RecebimentoRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O valor final da Receita é obrigatório");
    }

    @Test
    public void deveGerarViolacaoQuandoValorFinalNaoEhPositivo() {
        RecebimentoRequestDTO dto = new RecebimentoRequestDTO(List.of(1L), LocalDate.now(), BigDecimal.ZERO, 1L);

        Set<ConstraintViolation<RecebimentoRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("valorFinal");
    }

    @Test
    public void deveGerarViolacaoQuandoContaBancariaIdEhNula() {
        RecebimentoRequestDTO dto = new RecebimentoRequestDTO(List.of(1L), LocalDate.now(), BigDecimal.valueOf(1000), null);

        Set<ConstraintViolation<RecebimentoRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("A Conta Bancária é obrigatória");
    }
}
