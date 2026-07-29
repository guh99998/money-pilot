package com.gustavolopes.money_pilot.controller;

import com.gustavolopes.money_pilot.dto.BancoResponseDTO;
import com.gustavolopes.money_pilot.dto.CategoriaResponseDTO;
import com.gustavolopes.money_pilot.dto.ContaBancariaResponseDTO;
import com.gustavolopes.money_pilot.dto.ParceiroResponseDTO;
import com.gustavolopes.money_pilot.dto.RecebimentoRequestDTO;
import com.gustavolopes.money_pilot.dto.RecebimentoResponseDTO;
import com.gustavolopes.money_pilot.dto.ReceitaResponseDTO;
import com.gustavolopes.money_pilot.exception.ContaBancariaNotFoundException;
import com.gustavolopes.money_pilot.exception.RecebimentoNaoConfirmadoException;
import com.gustavolopes.money_pilot.exception.RecebimentoNaoEstornadoException;
import com.gustavolopes.money_pilot.exception.RecebimentoNotFoundException;
import com.gustavolopes.money_pilot.exception.ReceitaNaoEncontradaNaListaException;
import com.gustavolopes.money_pilot.model.Endereco;
import com.gustavolopes.money_pilot.model.StatusRecebimento;
import com.gustavolopes.money_pilot.model.TipoCategoria;
import com.gustavolopes.money_pilot.model.TipoDocumento;
import com.gustavolopes.money_pilot.model.TipoParceiro;
import com.gustavolopes.money_pilot.service.RecebimentoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import tools.jackson.databind.ObjectMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecebimentoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RecebimentoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    RecebimentoService service;

    private Endereco endereco() {
        return new Endereco(1L, "Rua das Flores", "100", "Centro", "12345-000", "São Paulo", "SP");
    }

    private ParceiroResponseDTO parceiro() {
        return new ParceiroResponseDTO(
                1L, "João", "11987654321", "joao@email.com", Set.of(TipoParceiro.CLIENTE),
                TipoDocumento.CPF, "12345678900", null, endereco()
        );
    }

    private CategoriaResponseDTO categoria() {
        return new CategoriaResponseDTO(1L, "Salário", "#FFFFFF", TipoCategoria.RECEITA);
    }

    private ReceitaResponseDTO receita(Long id) {
        return new ReceitaResponseDTO(
                id, "Salário", parceiro(), LocalDate.of(2026, 8, 5), 1,
                BigDecimal.valueOf(1000), categoria(), "Pagamento mensal"
        );
    }

    private BancoResponseDTO banco() {
        return new BancoResponseDTO(1L, "341", "Itaú Unibanco S.A.");
    }

    private ContaBancariaResponseDTO contaBancaria() {
        return new ContaBancariaResponseDTO(
                1L, banco(), "Conta Corrente", "0001", "123456-7",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(2000)
        );
    }

    private RecebimentoRequestDTO requestDTO() {
        return new RecebimentoRequestDTO(List.of(1L), LocalDate.of(2026, 7, 29), BigDecimal.valueOf(1000), 1L);
    }

    private RecebimentoResponseDTO responseDTO(Long id, StatusRecebimento status) {
        return new RecebimentoResponseDTO(
                id, List.of(receita(1L)), LocalDate.of(2026, 7, 29),
                BigDecimal.valueOf(1000), contaBancaria(), status
        );
    }

    @Test
    public void deveRetornar200EPaginaAoListarRecebimentos() throws Exception {
        when(service.getAllRecebimentos(any())).thenReturn(new PageImpl<>(List.of(responseDTO(1L, StatusRecebimento.CONFIRMADO))));

        mockMvc.perform(get("/recebimentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].status").value("CONFIRMADO"));
    }

    @Test
    public void deveRetornar200EBodyAoBuscarRecebimentoExistente() throws Exception {
        when(service.getRecebimentoById(1L)).thenReturn(responseDTO(1L, StatusRecebimento.CONFIRMADO));

        mockMvc.perform(get("/recebimentos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.receitas[0].id").value(1))
                .andExpect(jsonPath("$.contaBancaria.id").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMADO"));
    }

    @Test
    public void deveRetornar404AoBuscarRecebimentoInexistente() throws Exception {
        when(service.getRecebimentoById(999L)).thenThrow(new RecebimentoNotFoundException(999L));

        mockMvc.perform(get("/recebimentos/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("O Recebimento 999 não foi encontrado"));
    }

    @Test
    public void deveRetornar201AoConfirmarRecebimentoValido() throws Exception {
        when(service.createRecebimento(any(RecebimentoRequestDTO.class))).thenReturn(responseDTO(1L, StatusRecebimento.CONFIRMADO));

        mockMvc.perform(post("/recebimentos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMADO"));
    }

    @Test
    public void deveRetornar400AoConfirmarRecebimentoComListaDeReceitasVazia() throws Exception {
        RecebimentoRequestDTO requestInvalido = new RecebimentoRequestDTO(List.of(), LocalDate.of(2026, 7, 29), BigDecimal.valueOf(1000), 1L);

        mockMvc.perform(post("/recebimentos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Erro de validação"));
    }

    @Test
    public void deveRetornar404AoConfirmarRecebimentoComReceitaInexistente() throws Exception {
        when(service.createRecebimento(any(RecebimentoRequestDTO.class)))
                .thenThrow(new ReceitaNaoEncontradaNaListaException());

        mockMvc.perform(post("/recebimentos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar404AoConfirmarRecebimentoComContaBancariaInexistente() throws Exception {
        when(service.createRecebimento(any(RecebimentoRequestDTO.class)))
                .thenThrow(new ContaBancariaNotFoundException(999L));

        mockMvc.perform(post("/recebimentos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar200AoAtualizarRecebimentoEstornado() throws Exception {
        when(service.updateRecebimento(eq(1L), any(RecebimentoRequestDTO.class))).thenReturn(responseDTO(1L, StatusRecebimento.ESTORNADO));

        mockMvc.perform(put("/recebimentos/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ESTORNADO"));
    }

    @Test
    public void deveRetornar404AoAtualizarRecebimentoInexistente() throws Exception {
        when(service.updateRecebimento(eq(999L), any(RecebimentoRequestDTO.class)))
                .thenThrow(new RecebimentoNotFoundException(999L));

        mockMvc.perform(put("/recebimentos/{id}", 999L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar409AoAtualizarRecebimentoConfirmado() throws Exception {
        when(service.updateRecebimento(eq(1L), any(RecebimentoRequestDTO.class)))
                .thenThrow(new RecebimentoNaoEstornadoException(1L));

        mockMvc.perform(put("/recebimentos/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isConflict());
    }

    @Test
    public void deveRetornar204AoExcluirRecebimentoEstornado() throws Exception {
        doNothing().when(service).deleteRecebimento(1L);

        mockMvc.perform(delete("/recebimentos/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service).deleteRecebimento(1L);
    }

    @Test
    public void deveRetornar404AoExcluirRecebimentoInexistente() throws Exception {
        doThrow(new RecebimentoNotFoundException(999L)).when(service).deleteRecebimento(999L);

        mockMvc.perform(delete("/recebimentos/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar409AoExcluirRecebimentoConfirmado() throws Exception {
        doThrow(new RecebimentoNaoEstornadoException(1L)).when(service).deleteRecebimento(1L);

        mockMvc.perform(delete("/recebimentos/{id}", 1L))
                .andExpect(status().isConflict());
    }

    @Test
    public void deveRetornar200AoEstornarRecebimentoConfirmado() throws Exception {
        when(service.estornarRecebimento(1L)).thenReturn(responseDTO(1L, StatusRecebimento.ESTORNADO));

        mockMvc.perform(patch("/recebimentos/{id}/estornar", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ESTORNADO"));
    }

    @Test
    public void deveRetornar409AoEstornarRecebimentoJaEstornado() throws Exception {
        when(service.estornarRecebimento(1L)).thenThrow(new RecebimentoNaoConfirmadoException(1L));

        mockMvc.perform(patch("/recebimentos/{id}/estornar", 1L))
                .andExpect(status().isConflict());
    }

    @Test
    public void deveRetornar404AoEstornarRecebimentoInexistente() throws Exception {
        when(service.estornarRecebimento(999L)).thenThrow(new RecebimentoNotFoundException(999L));

        mockMvc.perform(patch("/recebimentos/{id}/estornar", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar200AoConfirmarRecebimentoEstornado() throws Exception {
        when(service.confirmarRecebimento(1L)).thenReturn(responseDTO(1L, StatusRecebimento.CONFIRMADO));

        mockMvc.perform(patch("/recebimentos/{id}/confirmar", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMADO"));
    }

    @Test
    public void deveRetornar409AoConfirmarRecebimentoJaConfirmado() throws Exception {
        when(service.confirmarRecebimento(1L)).thenThrow(new RecebimentoNaoEstornadoException(1L));

        mockMvc.perform(patch("/recebimentos/{id}/confirmar", 1L))
                .andExpect(status().isConflict());
    }

    @Test
    public void deveRetornar404AoConfirmarRecebimentoInexistente() throws Exception {
        when(service.confirmarRecebimento(999L)).thenThrow(new RecebimentoNotFoundException(999L));

        mockMvc.perform(patch("/recebimentos/{id}/confirmar", 999L))
                .andExpect(status().isNotFound());
    }
}
