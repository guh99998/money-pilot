package com.gustavolopes.money_pilot.controller;

import com.gustavolopes.money_pilot.dto.CategoriaResponseDTO;
import com.gustavolopes.money_pilot.dto.DespesaRequestDTO;
import com.gustavolopes.money_pilot.dto.DespesaResponseDTO;
import com.gustavolopes.money_pilot.dto.ParceiroResponseDTO;
import com.gustavolopes.money_pilot.exception.CategoriaNotFoundException;
import com.gustavolopes.money_pilot.exception.DespesaNotFoundException;
import com.gustavolopes.money_pilot.exception.ParceiroNotFoundException;
import com.gustavolopes.money_pilot.model.Endereco;
import com.gustavolopes.money_pilot.model.TipoCategoria;
import com.gustavolopes.money_pilot.model.TipoDocumento;
import com.gustavolopes.money_pilot.model.TipoParceiro;
import com.gustavolopes.money_pilot.service.DespesaService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DespesaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DespesaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    DespesaService service;

    private Endereco endereco() {
        return new Endereco(1L, "Rua das Flores", "100", "Centro", "12345-000", "São Paulo", "SP");
    }

    private ParceiroResponseDTO parceiro() {
        return new ParceiroResponseDTO(
                1L, "João", "11987654321", "joao@email.com", Set.of(TipoParceiro.FORNECEDOR),
                TipoDocumento.CPF, "12345678900", null, endereco()
        );
    }

    private CategoriaResponseDTO categoria() {
        return new CategoriaResponseDTO(1L, "Aluguel", "#FFFFFF", TipoCategoria.DESPESA);
    }

    private DespesaRequestDTO requestDTO() {
        return new DespesaRequestDTO(
                "Aluguel", 1L, LocalDate.of(2026, 8, 5), 1,
                BigDecimal.valueOf(1000), 1L, "Pagamento mensal"
        );
    }

    private DespesaResponseDTO responseDTO(Long id) {
        return new DespesaResponseDTO(
                id, "Aluguel", parceiro(), LocalDate.of(2026, 8, 5), 1,
                BigDecimal.valueOf(1000), categoria(), "Pagamento mensal"
        );
    }

    @Test
    public void deveRetornar200EPaginaAoListarDespesas() throws Exception {
        when(service.getAllDespesas(any())).thenReturn(new PageImpl<>(List.of(responseDTO(1L))));

        mockMvc.perform(get("/despesas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nomeDespesa").value("Aluguel"));
    }

    @Test
    public void deveRetornar200EBodyAoBuscarDespesaExistente() throws Exception {
        when(service.getDespesaById(1L)).thenReturn(responseDTO(1L));

        mockMvc.perform(get("/despesas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeDespesa").value("Aluguel"))
                .andExpect(jsonPath("$.parceiro.id").value(1))
                .andExpect(jsonPath("$.categoria.id").value(1));
    }

    @Test
    public void deveRetornar404AoBuscarDespesaInexistente() throws Exception {
        when(service.getDespesaById(999L)).thenThrow(new DespesaNotFoundException(999L));

        mockMvc.perform(get("/despesas/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("A Despesa 999 não foi encontrada"));
    }

    @Test
    public void deveRetornar201AoCriarDespesaValida() throws Exception {
        when(service.createDespesa(any(DespesaRequestDTO.class))).thenReturn(responseDTO(1L));

        mockMvc.perform(post("/despesas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeDespesa").value("Aluguel"));
    }

    @Test
    public void deveRetornar400AoCriarDespesaComNomeEmBranco() throws Exception {
        DespesaRequestDTO requestInvalido = new DespesaRequestDTO(
                "", 1L, LocalDate.of(2026, 8, 5), 1,
                BigDecimal.valueOf(1000), 1L, "Pagamento mensal"
        );

        mockMvc.perform(post("/despesas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Erro de validação"));
    }

    @Test
    public void deveRetornar404AoCriarDespesaComParceiroInexistente() throws Exception {
        when(service.createDespesa(any(DespesaRequestDTO.class)))
                .thenThrow(new ParceiroNotFoundException(999L));

        mockMvc.perform(post("/despesas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar404AoCriarDespesaComCategoriaInexistente() throws Exception {
        when(service.createDespesa(any(DespesaRequestDTO.class)))
                .thenThrow(new CategoriaNotFoundException(999L));

        mockMvc.perform(post("/despesas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar200AoAtualizarDespesaExistente() throws Exception {
        when(service.updateDespesa(eq(1L), any(DespesaRequestDTO.class))).thenReturn(responseDTO(1L));

        mockMvc.perform(put("/despesas/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeDespesa").value("Aluguel"));
    }

    @Test
    public void deveRetornar404AoAtualizarDespesaInexistente() throws Exception {
        when(service.updateDespesa(eq(999L), any(DespesaRequestDTO.class)))
                .thenThrow(new DespesaNotFoundException(999L));

        mockMvc.perform(put("/despesas/{id}", 999L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar204AoDeletarDespesaExistente() throws Exception {
        doNothing().when(service).deleteDespesa(1L);

        mockMvc.perform(delete("/despesas/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service).deleteDespesa(1L);
    }

    @Test
    public void deveRetornar404AoDeletarDespesaInexistente() throws Exception {
        doThrow(new DespesaNotFoundException(999L)).when(service).deleteDespesa(999L);

        mockMvc.perform(delete("/despesas/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
