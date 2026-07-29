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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class DespesaRequestDTOTest {

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

    private DespesaRequestDTO dtoValido() {
        return new DespesaRequestDTO(
                "Aluguel", 1L, LocalDate.now(), 1,
                BigDecimal.valueOf(1000), 1L, "Pagamento mensal"
        );
    }

    @Test
    public void naoDeveGerarViolacoesQuandoTodosOsCamposSaoValidos() {
        Set<ConstraintViolation<DespesaRequestDTO>> violacoes = validator.validate(dtoValido());

        assertThat(violacoes).isEmpty();
    }

    @Test
    public void deveGerarViolacaoQuandoNomeDespesaEstaEmBranco() {
        DespesaRequestDTO dto = new DespesaRequestDTO(
                "", 1L, LocalDate.now(), 1,
                BigDecimal.valueOf(1000), 1L, "Pagamento mensal"
        );

        Set<ConstraintViolation<DespesaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O nome da despesa é obrigatório");
    }

    @Test
    public void deveGerarViolacaoQuandoParceiroIdEhNulo() {
        DespesaRequestDTO dto = new DespesaRequestDTO(
                "Aluguel", null, LocalDate.now(), 1,
                BigDecimal.valueOf(1000), 1L, "Pagamento mensal"
        );

        Set<ConstraintViolation<DespesaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O Parceiro é obrigatório");
    }

    @Test
    public void deveGerarViolacaoQuandoDataVencimentoEhNula() {
        DespesaRequestDTO dto = new DespesaRequestDTO(
                "Aluguel", 1L, null, 1,
                BigDecimal.valueOf(1000), 1L, "Pagamento mensal"
        );

        Set<ConstraintViolation<DespesaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("A data de vencimento é obrigatória");
    }

    @Test
    public void deveGerarViolacaoQuandoNumeroParcelasEhMenorQueUm() {
        DespesaRequestDTO dto = new DespesaRequestDTO(
                "Aluguel", 1L, LocalDate.now(), 0,
                BigDecimal.valueOf(1000), 1L, "Pagamento mensal"
        );

        Set<ConstraintViolation<DespesaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("numeroParcelas");
    }

    @Test
    public void deveGerarViolacaoQuandoValorParcelaEhNulo() {
        DespesaRequestDTO dto = new DespesaRequestDTO(
                "Aluguel", 1L, LocalDate.now(), 1,
                null, 1L, "Pagamento mensal"
        );

        Set<ConstraintViolation<DespesaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O valor da parcela é obrigatório");
    }

    @Test
    public void deveGerarViolacaoQuandoValorParcelaNaoEhPositivo() {
        DespesaRequestDTO dto = new DespesaRequestDTO(
                "Aluguel", 1L, LocalDate.now(), 1,
                BigDecimal.ZERO, 1L, "Pagamento mensal"
        );

        Set<ConstraintViolation<DespesaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("valorParcela");
    }

    @Test
    public void deveGerarViolacaoQuandoCategoriaIdEhNula() {
        DespesaRequestDTO dto = new DespesaRequestDTO(
                "Aluguel", 1L, LocalDate.now(), 1,
                BigDecimal.valueOf(1000), null, "Pagamento mensal"
        );

        Set<ConstraintViolation<DespesaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("A categoria da despesa é obrigatória");
    }

    @Test
    public void deveGerarViolacaoQuandoObservacoesEhNula() {
        DespesaRequestDTO dto = new DespesaRequestDTO(
                "Aluguel", 1L, LocalDate.now(), 1,
                BigDecimal.valueOf(1000), 1L, null
        );

        Set<ConstraintViolation<DespesaRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("As observações da despesa são obrigatórias");
    }
}
