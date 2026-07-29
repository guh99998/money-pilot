package com.gustavolopes.money_pilot.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ContaBancariaRequestDTOTest {

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

    private ContaBancariaRequestDTO dtoValido() {
        return new ContaBancariaRequestDTO(1L, "Conta Corrente", "0001", "123456-7", BigDecimal.valueOf(1000));
    }

    @Test
    public void naoDeveGerarViolacoesQuandoTodosOsCamposSaoValidos() {
        Set<ConstraintViolation<ContaBancariaRequestDTO>> violacoes = validator.validate(dtoValido());

        assertThat(violacoes).isEmpty();
    }

    @Test
    public void naoDeveGerarViolacaoQuandoSaldoInicialEhZero() {
        ContaBancariaRequestDTO dto = new ContaBancariaRequestDTO(1L, "Conta Corrente", "0001", "123456-7", BigDecimal.ZERO);

        Set<ConstraintViolation<ContaBancariaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes).isEmpty();
    }

    @Test
    public void deveGerarViolacaoQuandoBancoIdEhNulo() {
        ContaBancariaRequestDTO dto = new ContaBancariaRequestDTO(null, "Conta Corrente", "0001", "123456-7", BigDecimal.valueOf(1000));

        Set<ConstraintViolation<ContaBancariaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O Banco é obrigatório");
    }

    @Test
    public void deveGerarViolacaoQuandoNomeContaBancariaEstaEmBranco() {
        ContaBancariaRequestDTO dto = new ContaBancariaRequestDTO(1L, "", "0001", "123456-7", BigDecimal.valueOf(1000));

        Set<ConstraintViolation<ContaBancariaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O nome da Conta Bancária é obrigatório");
    }

    @Test
    public void deveGerarViolacaoQuandoAgenciaEstaEmBranco() {
        ContaBancariaRequestDTO dto = new ContaBancariaRequestDTO(1L, "Conta Corrente", "", "123456-7", BigDecimal.valueOf(1000));

        Set<ConstraintViolation<ContaBancariaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O número da Agência é obrigatório");
    }

    @Test
    public void deveGerarViolacaoQuandoNumeroContaEstaEmBranco() {
        ContaBancariaRequestDTO dto = new ContaBancariaRequestDTO(1L, "Conta Corrente", "0001", "", BigDecimal.valueOf(1000));

        Set<ConstraintViolation<ContaBancariaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O Número da Conta Bancária é obrigatória");
    }

    @Test
    public void deveGerarViolacaoQuandoSaldoInicialEhNulo() {
        ContaBancariaRequestDTO dto = new ContaBancariaRequestDTO(1L, "Conta Corrente", "0001", "123456-7", null);

        Set<ConstraintViolation<ContaBancariaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O Saldo Inicial é obrigatório");
    }
}
