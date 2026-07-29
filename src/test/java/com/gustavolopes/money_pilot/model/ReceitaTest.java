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

public class ReceitaTest {

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

    private Receita criarReceitaValida() {
        Receita receita = new Receita();
        receita.setNomeReceita("Salário");
        receita.setDataVencimento(LocalDate.now());
        receita.setNumeroParcelas(1);
        receita.setValorParcela(BigDecimal.valueOf(1000));
        receita.setObservacoes("Pagamento mensal");
        return receita;
    }

    @Test
    public void naoDeveGerarViolacoesQuandoTodosOsCamposObrigatoriosEstaoPreenchidos() {
        Receita receita = criarReceitaValida();

        Set<ConstraintViolation<Receita>> violacoes = validator.validate(receita);

        assertThat(violacoes).isEmpty();
    }

    @Test
    public void deveGerarViolacaoQuandoNomeReceitaEstaEmBranco() {
        Receita receita = criarReceitaValida();
        receita.setNomeReceita("");

        Set<ConstraintViolation<Receita>> violacoes = validator.validate(receita);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("nomeReceita");
    }

    @Test
    public void deveGerarViolacaoQuandoDataVencimentoEhNula() {
        Receita receita = criarReceitaValida();
        receita.setDataVencimento(null);

        Set<ConstraintViolation<Receita>> violacoes = validator.validate(receita);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("dataVencimento");
    }

    @Test
    public void deveGerarViolacaoQuandoValorParcelaEhNulo() {
        Receita receita = criarReceitaValida();
        receita.setValorParcela(null);

        Set<ConstraintViolation<Receita>> violacoes = validator.validate(receita);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("valorParcela");
    }

    @Test
    public void deveGerarViolacaoQuandoObservacoesEhNula() {
        Receita receita = criarReceitaValida();
        receita.setObservacoes(null);

        Set<ConstraintViolation<Receita>> violacoes = validator.validate(receita);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("observacoes");
    }

    @Test
    public void naoDeveValidarNumeroMinimoDeParcelasPoisNotNullNaoAtuaSobrePrimitivos() {
        // numeroParcelas é um int primitivo: @NotNull nunca é violado (0 sempre "passa").
        // A regra de negócio (numeroParcelas >= 1) hoje só é garantida pelo @Min(1) do ReceitaRequestDTO.
        Receita receita = criarReceitaValida();
        receita.setNumeroParcelas(0);

        Set<ConstraintViolation<Receita>> violacoes = validator.validate(receita);

        assertThat(violacoes).isEmpty();
    }

    @Test
    public void deveConsiderarIguaisReceitasComMesmoIdIndependenteDosDemaisCampos() {
        Receita receita1 = criarReceitaValida();
        receita1.setId(1L);

        Receita receita2 = new Receita();
        receita2.setId(1L);
        receita2.setNomeReceita("Outro nome");

        assertThat(receita1).isEqualTo(receita2);
        assertThat(receita1.hashCode()).isEqualTo(receita2.hashCode());
    }

    @Test
    public void deveConsiderarDiferentesReceitasComIdsDiferentes() {
        Receita receita1 = criarReceitaValida();
        receita1.setId(1L);

        Receita receita2 = criarReceitaValida();
        receita2.setId(2L);

        assertThat(receita1).isNotEqualTo(receita2);
    }

    @Test
    public void deveExcluirRecebimentoDoToStringParaEvitarRecursaoComRecebimento() {
        Receita receita = criarReceitaValida();
        receita.setId(1L);

        Recebimento recebimento = new Recebimento();
        recebimento.setId(10L);
        recebimento.getReceitas().add(receita);
        receita.setRecebimento(recebimento);

        String textoReceita = receita.toString();

        assertThat(textoReceita).doesNotContain("recebimento=");
        // garante que o ciclo Receita -> Recebimento -> Receita não estoura em StackOverflowError
        assertThat(recebimento.toString()).contains("Receita");
    }
}
