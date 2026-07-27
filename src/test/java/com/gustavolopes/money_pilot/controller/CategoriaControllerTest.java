package com.gustavolopes.money_pilot.controller;

import com.gustavolopes.money_pilot.dto.CategoriaRequestDTO;
import com.gustavolopes.money_pilot.dto.CategoriaResponseDTO;
import com.gustavolopes.money_pilot.exception.CategoriaEmUsoException;
import com.gustavolopes.money_pilot.exception.CategoriaNotFoundException;
import com.gustavolopes.money_pilot.model.TipoCategoria;
import com.gustavolopes.money_pilot.service.CategoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import tools.jackson.databind.ObjectMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(CategoriaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoriaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CategoriaService service;

    @Test
    public void deveRetornar200EPaginaAoListarCategorias() throws Exception {
        CategoriaResponseDTO categoria = new CategoriaResponseDTO(1L, "Salário", "#00FF00", TipoCategoria.RECEITA);
        when(service.getAllCategorias(any())).thenReturn(new PageImpl<>(java.util.List.of(categoria)));

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("Salário"));
    }

    @Test
    public void deveRetornar200EBodyAoBuscarCategoriaExistente() throws Exception {
        CategoriaResponseDTO categoria = new CategoriaResponseDTO(1L, "Salário", "#00FF00", TipoCategoria.RECEITA);
        when(service.getCategoriaById(1L)).thenReturn(categoria);

        mockMvc.perform(get("/categorias/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Salário"))
                .andExpect(jsonPath("$.tagCor").value("#00FF00"))
                .andExpect(jsonPath("$.tipoCategoria").value("RECEITA"));
    }

    @Test
    public void deveRetornar404AoBuscarCategoriaInexistente() throws Exception {
        when(service.getCategoriaById(999L)).thenThrow(new CategoriaNotFoundException(999L));

        mockMvc.perform(get("/categorias/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("A categoria 999 não foi encontrada"));
    }

    @Test
    public void deveRetornar201AoCriarCategoriaValida() throws Exception {
        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO("Alimentação", "#00FF01", TipoCategoria.DESPESA);
        CategoriaResponseDTO responseDTO = new CategoriaResponseDTO(1L, "Alimentação", "#00FF01", TipoCategoria.DESPESA);

        when(service.createCategoria(any(CategoriaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/categorias")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Alimentação"));
    }

    @Test
    public void deveRetornar400AoCriarCategoriaComNomeEmBranco() throws Exception {
        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO("", "#00FF01", TipoCategoria.DESPESA);

        mockMvc.perform(post("/categorias")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Erro de validação"));
    }

    @Test
    public void deveRetornar400AoCriarCategoriaComCorInvalida() throws Exception {
        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO("Lazer", "cor-invalida", TipoCategoria.DESPESA);

        mockMvc.perform(post("/categorias")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deveRetornar200AoAtualizarCategoriaExistente() throws Exception {
        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO("Saúde", "#0000FF", TipoCategoria.DESPESA);
        CategoriaResponseDTO responseDTO = new CategoriaResponseDTO(3L, "Saúde", "#0000FF", TipoCategoria.DESPESA);

        when(service.updateCategoria(eq(3L), any(CategoriaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/categorias/{id}", 3L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Saúde"));
    }

    @Test
    public void deveRetornar404AoAtualizarCategoriaInexistente() throws Exception {
        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO("Saúde", "#0000FF", TipoCategoria.DESPESA);

        when(service.updateCategoria(eq(999L), any(CategoriaRequestDTO.class)))
                .thenThrow(new CategoriaNotFoundException(999L));

        mockMvc.perform(put("/categorias/{id}", 999L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar204AoDeletarCategoriaExistente() throws Exception {
        doNothing().when(service).deleteCategoria(1L);

        mockMvc.perform(delete("/categorias/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service).deleteCategoria(1L);
    }

    @Test
    public void deveRetornar409AoDeletarCategoriaEmUso() throws Exception {
        doThrow(new CategoriaEmUsoException(1L)).when(service).deleteCategoria(1L);

        mockMvc.perform(delete("/categorias/{id}", 1L))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("A categoria 1 está associada a uma ou mais receitas e não pode ser removida"));
    }

    @Test
    public void deveRetornar404AoDeletarCategoriaInexistente() throws Exception {
        doThrow(new CategoriaNotFoundException(999L)).when(service).deleteCategoria(999L);

        mockMvc.perform(delete("/categorias/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
