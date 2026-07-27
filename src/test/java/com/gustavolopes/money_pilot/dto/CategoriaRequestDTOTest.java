package com.gustavolopes.money_pilot.dto;

import com.gustavolopes.money_pilot.model.TipoCategoria;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoriaRequestDTOTest {

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

    @Test
    public void naoDeveGerarViolacoesQuandoTodosOsCamposSaoValidos() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO("Lazer", "#FF5733", TipoCategoria.DESPESA);

        Set<ConstraintViolation<CategoriaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes).isEmpty();
    }

    @Test
    public void deveGerarViolacaoQuandoNomeEstaEmBranco() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO("", "#FF5733", TipoCategoria.DESPESA);

        Set<ConstraintViolation<CategoriaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O nome da categoria é obrigatório");
    }

    @Test
    public void deveGerarViolacaoQuandoTagCorEstaEmBranco() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO("Lazer", "", TipoCategoria.DESPESA);

        Set<ConstraintViolation<CategoriaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("A cor da tag é obrigatória");
    }

    @Test
    public void deveGerarViolacaoQuandoTagCorNaoEhHexadecimalValido() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO("Lazer", "cor-invalida", TipoCategoria.DESPESA);

        Set<ConstraintViolation<CategoriaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("A tag de cor deve ser um código Hexadecimal válido (ex: #FF5733)");
    }

    @Test
    public void deveAceitarTagCorNoFormatoHexadecimalCurto() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO("Lazer", "#F53", TipoCategoria.DESPESA);

        Set<ConstraintViolation<CategoriaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes).isEmpty();
    }

    @Test
    public void deveGerarViolacaoQuandoTipoCategoriaEhNulo() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO("Lazer", "#FF5733", null);

        Set<ConstraintViolation<CategoriaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O tipo da Categoria é obrigatório");
    }
}
