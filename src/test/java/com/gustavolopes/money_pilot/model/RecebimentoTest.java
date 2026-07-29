package com.gustavolopes.money_pilot.model;

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

public class RecebimentoTest {

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

    private Recebimento criarRecebimentoValido() {
        Recebimento recebimento = new Recebimento();
        recebimento.setDataRecebimento(LocalDate.now());
        recebimento.setValorFinal(BigDecimal.valueOf(1000));
        recebimento.setStatus(StatusRecebimento.CONFIRMADO);
        return recebimento;
    }

    @Test
    public void naoDeveGerarViolacoesQuandoTodosOsCamposObrigatoriosEstaoPreenchidos() {
        Recebimento recebimento = criarRecebimentoValido();

        Set<ConstraintViolation<Recebimento>> violacoes = validator.validate(recebimento);

        assertThat(violacoes).isEmpty();
    }

    @Test
    public void deveGerarViolacaoQuandoDataRecebimentoEhNula() {
        Recebimento recebimento = criarRecebimentoValido();
        recebimento.setDataRecebimento(null);

        Set<ConstraintViolation<Recebimento>> violacoes = validator.validate(recebimento);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("dataRecebimento");
    }

    @Test
    public void deveGerarViolacaoQuandoValorFinalEhNulo() {
        Recebimento recebimento = criarRecebimentoValido();
        recebimento.setValorFinal(null);

        Set<ConstraintViolation<Recebimento>> violacoes = validator.validate(recebimento);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("valorFinal");
    }

    @Test
    public void deveGerarViolacaoQuandoStatusEhNulo() {
        Recebimento recebimento = criarRecebimentoValido();
        recebimento.setStatus(null);

        Set<ConstraintViolation<Recebimento>> violacoes = validator.validate(recebimento);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("status");
    }

    @Test
    public void deveConsiderarIguaisRecebimentosComMesmoIdIndependenteDosDemaisCampos() {
        Recebimento recebimento1 = criarRecebimentoValido();
        recebimento1.setId(1L);

        Recebimento recebimento2 = new Recebimento();
        recebimento2.setId(1L);
        recebimento2.setValorFinal(BigDecimal.valueOf(2000));

        assertThat(recebimento1).isEqualTo(recebimento2);
        assertThat(recebimento1.hashCode()).isEqualTo(recebimento2.hashCode());
    }

    @Test
    public void deveConsiderarDiferentesRecebimentosComIdsDiferentes() {
        Recebimento recebimento1 = criarRecebimentoValido();
        recebimento1.setId(1L);

        Recebimento recebimento2 = criarRecebimentoValido();
        recebimento2.setId(2L);

        assertThat(recebimento1).isNotEqualTo(recebimento2);
    }

    @Test
    public void deveVincularReceitaAoRecebimentoAtravesDaListaReceitas() {
        Recebimento recebimento = criarRecebimentoValido();
        recebimento.setId(1L);

        Receita receita = new Receita();
        receita.setId(10L);
        receita.setRecebimento(recebimento);
        recebimento.getReceitas().add(receita);

        assertThat(recebimento.getReceitas()).containsExactly(receita);
        assertThat(receita.getRecebimento()).isEqualTo(recebimento);
    }
}
