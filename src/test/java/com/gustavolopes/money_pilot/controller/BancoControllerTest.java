package com.gustavolopes.money_pilot.controller;

import com.gustavolopes.money_pilot.dto.BancoRequestDTO;
import com.gustavolopes.money_pilot.dto.BancoResponseDTO;
import com.gustavolopes.money_pilot.exception.BancoCodigoImutavelException;
import com.gustavolopes.money_pilot.exception.BancoEmUsoException;
import com.gustavolopes.money_pilot.exception.BancoNotFoundException;
import com.gustavolopes.money_pilot.service.BancoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import tools.jackson.databind.ObjectMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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

@WebMvcTest(BancoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BancoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    BancoService service;

    private BancoRequestDTO requestDTO() {
        return new BancoRequestDTO("341", "Itaú Unibanco S.A.");
    }

    private BancoResponseDTO responseDTO(Long id) {
        return new BancoResponseDTO(id, "341", "Itaú Unibanco S.A.");
    }

    @Test
    public void deveRetornar200EPaginaAoListarBancos() throws Exception {
        when(service.getAllBancos(any())).thenReturn(new PageImpl<>(List.of(responseDTO(1L))));

        mockMvc.perform(get("/bancos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nomeBanco").value("Itaú Unibanco S.A."));
    }

    @Test
    public void deveRetornar200EBodyAoBuscarBancoExistente() throws Exception {
        when(service.getBancoById(1L)).thenReturn(responseDTO(1L));

        mockMvc.perform(get("/bancos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigoBanco").value("341"))
                .andExpect(jsonPath("$.nomeBanco").value("Itaú Unibanco S.A."));
    }

    @Test
    public void deveRetornar404AoBuscarBancoInexistente() throws Exception {
        when(service.getBancoById(999L)).thenThrow(new BancoNotFoundException(999L));

        mockMvc.perform(get("/bancos/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("O Banco 999 não foi encontrado"));
    }

    @Test
    public void deveRetornar201AoCriarBancoValido() throws Exception {
        when(service.createBanco(any(BancoRequestDTO.class))).thenReturn(responseDTO(1L));

        mockMvc.perform(post("/bancos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigoBanco").value("341"));
    }

    @Test
    public void deveRetornar400AoCriarBancoComCodigoEmBranco() throws Exception {
        BancoRequestDTO requestInvalido = new BancoRequestDTO("", "Itaú Unibanco S.A.");

        mockMvc.perform(post("/bancos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Erro de validação"));
    }

    @Test
    public void deveRetornar400AoCriarBancoComNomeEmBranco() throws Exception {
        BancoRequestDTO requestInvalido = new BancoRequestDTO("341", "");

        mockMvc.perform(post("/bancos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Erro de validação"));
    }

    @Test
    public void deveRetornar200AoAtualizarBancoExistente() throws Exception {
        when(service.updateBanco(eq(1L), any(BancoRequestDTO.class))).thenReturn(responseDTO(1L));

        mockMvc.perform(put("/bancos/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeBanco").value("Itaú Unibanco S.A."));
    }

    @Test
    public void deveRetornar400AoAtualizarBancoComCodigoAlterado() throws Exception {
        when(service.updateBanco(eq(1L), any(BancoRequestDTO.class)))
                .thenThrow(new BancoCodigoImutavelException(1L));

        mockMvc.perform(put("/bancos/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deveRetornar404AoAtualizarBancoInexistente() throws Exception {
        when(service.updateBanco(eq(999L), any(BancoRequestDTO.class)))
                .thenThrow(new BancoNotFoundException(999L));

        mockMvc.perform(put("/bancos/{id}", 999L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar204AoDeletarBancoExistente() throws Exception {
        doNothing().when(service).deleteBanco(1L);

        mockMvc.perform(delete("/bancos/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service).deleteBanco(1L);
    }

    @Test
    public void deveRetornar404AoDeletarBancoInexistente() throws Exception {
        doThrow(new BancoNotFoundException(999L)).when(service).deleteBanco(999L);

        mockMvc.perform(delete("/bancos/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar409AoDeletarBancoEmUso() throws Exception {
        doThrow(new BancoEmUsoException(1L)).when(service).deleteBanco(1L);

        mockMvc.perform(delete("/bancos/{id}", 1L))
                .andExpect(status().isConflict());
    }
}
