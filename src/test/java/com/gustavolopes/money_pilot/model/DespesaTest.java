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

public class DespesaTest {

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

    private Despesa criarDespesaValida() {
        Despesa despesa = new Despesa();
        despesa.setNomeDespesa("Aluguel");
        despesa.setDataVencimento(LocalDate.now());
        despesa.setNumeroParcelas(1);
        despesa.setValorParcela(BigDecimal.valueOf(1000));
        despesa.setObservacoes("Pagamento mensal");
        return despesa;
    }

    @Test
    public void naoDeveGerarViolacoesQuandoTodosOsCamposObrigatoriosEstaoPreenchidos() {
        Despesa despesa = criarDespesaValida();

        Set<ConstraintViolation<Despesa>> violacoes = validator.validate(despesa);

        assertThat(violacoes).isEmpty();
    }

    @Test
    public void deveGerarViolacaoQuandoNomeDespesaEstaEmBranco() {
        Despesa despesa = criarDespesaValida();
        despesa.setNomeDespesa("");

        Set<ConstraintViolation<Despesa>> violacoes = validator.validate(despesa);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("nomeDespesa");
    }

    @Test
    public void deveGerarViolacaoQuandoDataVencimentoEhNula() {
        Despesa despesa = criarDespesaValida();
        despesa.setDataVencimento(null);

        Set<ConstraintViolation<Despesa>> violacoes = validator.validate(despesa);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("dataVencimento");
    }

    @Test
    public void deveGerarViolacaoQuandoValorParcelaEhNulo() {
        Despesa despesa = criarDespesaValida();
        despesa.setValorParcela(null);

        Set<ConstraintViolation<Despesa>> violacoes = validator.validate(despesa);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("valorParcela");
    }

    @Test
    public void deveGerarViolacaoQuandoObservacoesEhNula() {
        Despesa despesa = criarDespesaValida();
        despesa.setObservacoes(null);

        Set<ConstraintViolation<Despesa>> violacoes = validator.validate(despesa);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .contains("observacoes");
    }

    @Test
    public void naoDeveValidarNumeroMinimoDeParcelasPoisNotNullNaoAtuaSobrePrimitivos() {
        // numeroParcelas é um int primitivo: @NotNull nunca é violado (0 sempre "passa").
        // A regra de negócio (numeroParcelas >= 1) hoje só é garantida pelo @Min(1) do DespesaRequestDTO.
        Despesa despesa = criarDespesaValida();
        despesa.setNumeroParcelas(0);

        Set<ConstraintViolation<Despesa>> violacoes = validator.validate(despesa);

        assertThat(violacoes).isEmpty();
    }

    @Test
    public void deveConsiderarIguaisDespesasComMesmoIdIndependenteDosDemaisCampos() {
        Despesa despesa1 = criarDespesaValida();
        despesa1.setId(1L);

        Despesa despesa2 = new Despesa();
        despesa2.setId(1L);
        despesa2.setNomeDespesa("Outro nome");

        assertThat(despesa1).isEqualTo(despesa2);
        assertThat(despesa1.hashCode()).isEqualTo(despesa2.hashCode());
    }

    @Test
    public void deveConsiderarDiferentesDespesasComIdsDiferentes() {
        Despesa despesa1 = criarDespesaValida();
        despesa1.setId(1L);

        Despesa despesa2 = criarDespesaValida();
        despesa2.setId(2L);

        assertThat(despesa1).isNotEqualTo(despesa2);
    }

    @Test
    public void deveExcluirPagamentoDoToStringParaEvitarRecursaoComPagamento() {
        Despesa despesa = criarDespesaValida();
        despesa.setId(1L);

        Pagamento pagamento = new Pagamento();
        pagamento.setId(10L);
        pagamento.getDespesas().add(despesa);
        despesa.setPagamento(pagamento);

        String textoDespesa = despesa.toString();

        assertThat(textoDespesa).doesNotContain("pagamento=");
        // garante que o ciclo Despesa -> Pagamento -> Despesa não estoura em StackOverflowError
        assertThat(pagamento.toString()).contains("Despesa");
    }
}
