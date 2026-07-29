package com.gustavolopes.money_pilot.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class BancoRequestDTOTest {

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

    private BancoRequestDTO dtoValido() {
        return new BancoRequestDTO("341", "Itaú Unibanco S.A.");
    }

    @Test
    public void naoDeveGerarViolacoesQuandoTodosOsCamposSaoValidos() {
        Set<ConstraintViolation<BancoRequestDTO>> violacoes = validator.validate(dtoValido());

        assertThat(violacoes).isEmpty();
    }

    @Test
    public void deveGerarViolacaoQuandoCodigoBancoEstaEmBranco() {
        BancoRequestDTO dto = new BancoRequestDTO("", "Itaú Unibanco S.A.");

        Set<ConstraintViolation<BancoRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O código do banco é obrigatório");
    }

    @Test
    public void deveGerarViolacaoQuandoNomeBancoEstaEmBranco() {
        BancoRequestDTO dto = new BancoRequestDTO("341", "");

        Set<ConstraintViolation<BancoRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O nome do banco é obrigatório");
    }
}
