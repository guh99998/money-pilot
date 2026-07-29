package com.gustavolopes.money_pilot.model;

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

public class ContaBancariaTest {

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

    private Banco criarBanco() {
        Banco banco = new Banco();
        banco.setId(1L);
        banco.setCodigoBanco("341");
        banco.setNomeBanco("Itaú Unibanco S.A.");
        return banco;
    }

    private ContaBancaria criarContaBancariaValida() {
        ContaBancaria contaBancaria = new ContaBancaria();
        contaBancaria.setBanco(criarBanco());
        contaBancaria.setNomeContaBancaria("Conta Corrente");
        contaBancaria.setAgencia("0001");
        contaBancaria.setNumeroConta("123456-7");
        contaBancaria.setSaldoInicial(BigDecimal.valueOf(1000));
        return contaBancaria;
    }

    @Test
    public void naoDeveGerarViolacoesQuandoTodosOsCamposObrigatoriosEstaoPreenchidos() {
        ContaBancaria contaBancaria = criarContaBancariaValida();

        Set<ConstraintViolation<ContaBancaria>> violacoes = validator.validate(contaBancaria);

        assertThat(violacoes).isEmpty();
    }

    @Test
    public void deveGerarViolacaoQuandoNomeContaBancariaEstaEmBranco() {
        ContaBancaria contaBancaria = criarContaBancariaValida();
        contaBancaria.setNomeContaBancaria("");

        Set<ConstraintViolation<ContaBancaria>> violacoes = validator.validate(contaBancaria);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("nomeContaBancaria");
    }

    @Test
    public void deveGerarViolacaoQuandoAgenciaEstaEmBranco() {
        ContaBancaria contaBancaria = criarContaBancariaValida();
        contaBancaria.setAgencia("");

        Set<ConstraintViolation<ContaBancaria>> violacoes = validator.validate(contaBancaria);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("agencia");
    }

    @Test
    public void deveGerarViolacaoQuandoNumeroContaEstaEmBranco() {
        ContaBancaria contaBancaria = criarContaBancariaValida();
        contaBancaria.setNumeroConta("");

        Set<ConstraintViolation<ContaBancaria>> violacoes = validator.validate(contaBancaria);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("numeroConta");
    }

    @Test
    public void deveGerarViolacaoQuandoSaldoInicialEhNulo() {
        ContaBancaria contaBancaria = criarContaBancariaValida();
        contaBancaria.setSaldoInicial(null);

        Set<ConstraintViolation<ContaBancaria>> violacoes = validator.validate(contaBancaria);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("saldoInicial");
    }

    @Test
    public void deveConsiderarIguaisContasBancariasComMesmoIdIndependenteDosDemaisCampos() {
        ContaBancaria contaBancaria1 = criarContaBancariaValida();
        contaBancaria1.setId(1L);

        ContaBancaria contaBancaria2 = new ContaBancaria();
        contaBancaria2.setId(1L);
        contaBancaria2.setNomeContaBancaria("Outro nome");

        assertThat(contaBancaria1).isEqualTo(contaBancaria2);
        assertThat(contaBancaria1.hashCode()).isEqualTo(contaBancaria2.hashCode());
    }

    @Test
    public void deveConsiderarDiferentesContasBancariasComIdsDiferentes() {
        ContaBancaria contaBancaria1 = criarContaBancariaValida();
        contaBancaria1.setId(1L);

        ContaBancaria contaBancaria2 = criarContaBancariaValida();
        contaBancaria2.setId(2L);

        assertThat(contaBancaria1).isNotEqualTo(contaBancaria2);
    }
}
