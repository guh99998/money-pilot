package com.gustavolopes.money_pilot.controller;

import com.gustavolopes.money_pilot.dto.CategoriaResponseDTO;
import com.gustavolopes.money_pilot.dto.ParceiroResponseDTO;
import com.gustavolopes.money_pilot.dto.ReceitaRequestDTO;
import com.gustavolopes.money_pilot.dto.ReceitaResponseDTO;
import com.gustavolopes.money_pilot.exception.CategoriaNotFoundException;
import com.gustavolopes.money_pilot.exception.ParceiroNotFoundException;
import com.gustavolopes.money_pilot.exception.ReceitaNotFoundException;
import com.gustavolopes.money_pilot.model.Endereco;
import com.gustavolopes.money_pilot.model.TipoCategoria;
import com.gustavolopes.money_pilot.model.TipoDocumento;
import com.gustavolopes.money_pilot.model.TipoParceiro;
import com.gustavolopes.money_pilot.service.ReceitaService;
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

@WebMvcTest(ReceitaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReceitaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ReceitaService service;

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

    private ReceitaRequestDTO requestDTO() {
        return new ReceitaRequestDTO(
                "Salário", 1L, LocalDate.of(2026, 8, 5), 1,
                BigDecimal.valueOf(1000), 1L, "Pagamento mensal"
        );
    }

    private ReceitaResponseDTO responseDTO(Long id) {
        return new ReceitaResponseDTO(
                id, "Salário", parceiro(), LocalDate.of(2026, 8, 5), 1,
                BigDecimal.valueOf(1000), categoria(), "Pagamento mensal"
        );
    }

    @Test
    public void deveRetornar200EPaginaAoListarReceitas() throws Exception {
        when(service.getAllReceitas(any())).thenReturn(new PageImpl<>(List.of(responseDTO(1L))));

        mockMvc.perform(get("/receitas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nomeReceita").value("Salário"));
    }

    @Test
    public void deveRetornar200EBodyAoBuscarReceitaExistente() throws Exception {
        when(service.getReceitaById(1L)).thenReturn(responseDTO(1L));

        mockMvc.perform(get("/receitas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeReceita").value("Salário"))
                .andExpect(jsonPath("$.parceiro.id").value(1))
                .andExpect(jsonPath("$.categoria.id").value(1));
    }

    @Test
    public void deveRetornar404AoBuscarReceitaInexistente() throws Exception {
        when(service.getReceitaById(999L)).thenThrow(new ReceitaNotFoundException(999L));

        mockMvc.perform(get("/receitas/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("A Receita 999 não foi encontrada"));
    }

    @Test
    public void deveRetornar201AoCriarReceitaValida() throws Exception {
        when(service.createReceita(any(ReceitaRequestDTO.class))).thenReturn(responseDTO(1L));

        mockMvc.perform(post("/receitas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeReceita").value("Salário"));
    }

    @Test
    public void deveRetornar400AoCriarReceitaComNomeEmBranco() throws Exception {
        ReceitaRequestDTO requestInvalido = new ReceitaRequestDTO(
                "", 1L, LocalDate.of(2026, 8, 5), 1,
                BigDecimal.valueOf(1000), 1L, "Pagamento mensal"
        );

        mockMvc.perform(post("/receitas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Erro de validação"));
    }

    @Test
    public void deveRetornar404AoCriarReceitaComParceiroInexistente() throws Exception {
        when(service.createReceita(any(ReceitaRequestDTO.class)))
                .thenThrow(new ParceiroNotFoundException(999L));

        mockMvc.perform(post("/receitas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar404AoCriarReceitaComCategoriaInexistente() throws Exception {
        when(service.createReceita(any(ReceitaRequestDTO.class)))
                .thenThrow(new CategoriaNotFoundException(999L));

        mockMvc.perform(post("/receitas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar200AoAtualizarReceitaExistente() throws Exception {
        when(service.updateReceita(eq(1L), any(ReceitaRequestDTO.class))).thenReturn(responseDTO(1L));

        mockMvc.perform(put("/receitas/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeReceita").value("Salário"));
    }

    @Test
    public void deveRetornar404AoAtualizarReceitaInexistente() throws Exception {
        when(service.updateReceita(eq(999L), any(ReceitaRequestDTO.class)))
                .thenThrow(new ReceitaNotFoundException(999L));

        mockMvc.perform(put("/receitas/{id}", 999L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar204AoDeletarReceitaExistente() throws Exception {
        doNothing().when(service).deleteReceita(1L);

        mockMvc.perform(delete("/receitas/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service).deleteReceita(1L);
    }

    @Test
    public void deveRetornar404AoDeletarReceitaInexistente() throws Exception {
        doThrow(new ReceitaNotFoundException(999L)).when(service).deleteReceita(999L);

        mockMvc.perform(delete("/receitas/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
