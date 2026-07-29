package com.gustavolopes.money_pilot.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class BancoTest {

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

    private Banco criarBancoValido() {
        Banco banco = new Banco();
        banco.setCodigoBanco("341");
        banco.setNomeBanco("Itaú Unibanco S.A.");
        return banco;
    }

    @Test
    public void naoDeveGerarViolacoesQuandoTodosOsCamposObrigatoriosEstaoPreenchidos() {
        Banco banco = criarBancoValido();

        Set<ConstraintViolation<Banco>> violacoes = validator.validate(banco);

        assertThat(violacoes).isEmpty();
    }

    @Test
    public void deveGerarViolacaoQuandoCodigoBancoEstaEmBranco() {
        Banco banco = criarBancoValido();
        banco.setCodigoBanco("");

        Set<ConstraintViolation<Banco>> violacoes = validator.validate(banco);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("codigoBanco");
    }

    @Test
    public void deveGerarViolacaoQuandoNomeBancoEstaEmBranco() {
        Banco banco = criarBancoValido();
        banco.setNomeBanco("");

        Set<ConstraintViolation<Banco>> violacoes = validator.validate(banco);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("nomeBanco");
    }

    @Test
    public void deveConsiderarIguaisBancosComMesmosCampos() {
        Banco banco1 = criarBancoValido();
        banco1.setId(1L);

        Banco banco2 = criarBancoValido();
        banco2.setId(1L);

        assertThat(banco1).isEqualTo(banco2);
        assertThat(banco1.hashCode()).isEqualTo(banco2.hashCode());
    }

    @Test
    public void deveConsiderarDiferentesBancosComCodigosDiferentes() {
        Banco banco1 = criarBancoValido();
        banco1.setId(1L);

        Banco banco2 = criarBancoValido();
        banco2.setId(1L);
        banco2.setCodigoBanco("001");

        assertThat(banco1).isNotEqualTo(banco2);
    }
}
