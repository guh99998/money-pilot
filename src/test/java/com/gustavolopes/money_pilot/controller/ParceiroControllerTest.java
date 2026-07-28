package com.gustavolopes.money_pilot.controller;

import com.gustavolopes.money_pilot.dto.ParceiroRequestDTO;
import com.gustavolopes.money_pilot.dto.ParceiroResponseDTO;
import com.gustavolopes.money_pilot.exception.DocumentoImutavelException;
import com.gustavolopes.money_pilot.exception.DocumentoInvalidoException;
import com.gustavolopes.money_pilot.exception.ParceiroEmUsoException;
import com.gustavolopes.money_pilot.exception.ParceiroNotFoundException;
import com.gustavolopes.money_pilot.model.Endereco;
import com.gustavolopes.money_pilot.model.TipoDocumento;
import com.gustavolopes.money_pilot.model.TipoParceiro;
import com.gustavolopes.money_pilot.service.ParceiroService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import tools.jackson.databind.ObjectMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(ParceiroController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ParceiroControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ParceiroService service;

    private Endereco endereco() {
        return new Endereco(1L, "Rua das Flores", "100", "Centro", "12345-000", "São Paulo", "SP");
    }

    private ParceiroRequestDTO requestDTOPF() {
        return new ParceiroRequestDTO(
                "João", "11987654321", "joao@email.com", Set.of(TipoParceiro.CLIENTE),
                TipoDocumento.CPF, "12345678900", null, endereco()
        );
    }

    private ParceiroResponseDTO responseDTOPF(Long id) {
        return new ParceiroResponseDTO(
                id, "João", "11987654321", "joao@email.com", Set.of(TipoParceiro.CLIENTE),
                TipoDocumento.CPF, "12345678900", null, endereco()
        );
    }

    @Test
    public void deveRetornar200EPaginaAoListarParceiros() throws Exception {
        when(service.getAllParceiros(any())).thenReturn(new PageImpl<>(List.of(responseDTOPF(1L))));

        mockMvc.perform(get("/parceiros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("João"));
    }

    @Test
    public void deveRetornar200EBodyAoBuscarParceiroExistente() throws Exception {
        when(service.getParceiroById(1L)).thenReturn(responseDTOPF(1L));

        mockMvc.perform(get("/parceiros/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipoDocumento").value("CPF"))
                .andExpect(jsonPath("$.cpf").value("12345678900"));
    }

    @Test
    public void deveRetornar404AoBuscarParceiroInexistente() throws Exception {
        when(service.getParceiroById(999L)).thenThrow(new ParceiroNotFoundException(999L));

        mockMvc.perform(get("/parceiros/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("O Parceiro 999 não foi encontrado"));
    }

    @Test
    public void deveRetornar201AoCriarParceiroValido() throws Exception {
        when(service.createParceiro(any(ParceiroRequestDTO.class))).thenReturn(responseDTOPF(1L));

        mockMvc.perform(post("/parceiros")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTOPF())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João"));
    }

    @Test
    public void deveRetornar400AoCriarParceiroComNomeEmBranco() throws Exception {
        ParceiroRequestDTO requestInvalido = new ParceiroRequestDTO(
                "", "11987654321", "joao@email.com", Set.of(TipoParceiro.CLIENTE),
                TipoDocumento.CPF, "12345678900", null, endereco()
        );

        mockMvc.perform(post("/parceiros")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Erro de validação"));
    }

    @Test
    public void deveRetornar400AoCriarParceiroComDocumentoInvalido() throws Exception {
        when(service.createParceiro(any(ParceiroRequestDTO.class)))
                .thenThrow(new DocumentoInvalidoException(TipoDocumento.CPF));

        mockMvc.perform(post("/parceiros")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTOPF())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deveRetornar200AoAtualizarParceiroExistente() throws Exception {
        when(service.updateParceiro(eq(1L), any(ParceiroRequestDTO.class))).thenReturn(responseDTOPF(1L));

        mockMvc.perform(put("/parceiros/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTOPF())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João"));
    }

    @Test
    public void deveRetornar404AoAtualizarParceiroInexistente() throws Exception {
        when(service.updateParceiro(eq(999L), any(ParceiroRequestDTO.class)))
                .thenThrow(new ParceiroNotFoundException(999L));

        mockMvc.perform(put("/parceiros/{id}", 999L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTOPF())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar400AoAtualizarComDocumentoImutavelDivergente() throws Exception {
        when(service.updateParceiro(eq(1L), any(ParceiroRequestDTO.class)))
                .thenThrow(new DocumentoImutavelException(1L));

        mockMvc.perform(put("/parceiros/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTOPF())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deveRetornar204AoDeletarParceiroExistente() throws Exception {
        doNothing().when(service).deleteParceiro(1L);

        mockMvc.perform(delete("/parceiros/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service).deleteParceiro(1L);
    }

    @Test
    public void deveRetornar409AoDeletarParceiroEmUso() throws Exception {
        doThrow(new ParceiroEmUsoException(1L)).when(service).deleteParceiro(1L);

        mockMvc.perform(delete("/parceiros/{id}", 1L))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("O Parceiro 1 está associado a uma ou mais receitas/despesas e não pode ser removido"));
    }

    @Test
    public void deveRetornar404AoDeletarParceiroInexistente() throws Exception {
        doThrow(new ParceiroNotFoundException(999L)).when(service).deleteParceiro(999L);

        mockMvc.perform(delete("/parceiros/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
