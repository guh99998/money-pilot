package com.gustavolopes.money_pilot.controller;

import com.gustavolopes.money_pilot.dto.BancoResponseDTO;
import com.gustavolopes.money_pilot.dto.ContaBancariaRequestDTO;
import com.gustavolopes.money_pilot.dto.ContaBancariaResponseDTO;
import com.gustavolopes.money_pilot.exception.BancoNotFoundException;
import com.gustavolopes.money_pilot.exception.ContaBancariaEmUsoException;
import com.gustavolopes.money_pilot.exception.ContaBancariaNotFoundException;
import com.gustavolopes.money_pilot.service.ContaBancariaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import tools.jackson.databind.ObjectMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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

@WebMvcTest(ContaBancariaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ContaBancariaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ContaBancariaService service;

    private BancoResponseDTO banco() {
        return new BancoResponseDTO(1L, "341", "Itaú Unibanco S.A.");
    }

    private ContaBancariaRequestDTO requestDTO() {
        return new ContaBancariaRequestDTO(1L, "Conta Corrente", "0001", "123456-7", BigDecimal.valueOf(1000));
    }

    private ContaBancariaResponseDTO responseDTO(Long id) {
        return new ContaBancariaResponseDTO(id, banco(), "Conta Corrente", "0001", "123456-7", BigDecimal.valueOf(1000));
    }

    @Test
    public void deveRetornar200EPaginaAoListarContasBancarias() throws Exception {
        when(service.getAllContasBancarias(any())).thenReturn(new PageImpl<>(List.of(responseDTO(1L))));

        mockMvc.perform(get("/contasbancarias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nomeContaBancaria").value("Conta Corrente"));
    }

    @Test
    public void deveRetornar200EBodyAoBuscarContaBancariaExistente() throws Exception {
        when(service.getContaBancariById(1L)).thenReturn(responseDTO(1L));

        mockMvc.perform(get("/contasbancarias/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeContaBancaria").value("Conta Corrente"))
                .andExpect(jsonPath("$.banco.id").value(1));
    }

    @Test
    public void deveRetornar404AoBuscarContaBancariaInexistente() throws Exception {
        when(service.getContaBancariById(999L)).thenThrow(new ContaBancariaNotFoundException(999L));

        mockMvc.perform(get("/contasbancarias/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("A conta bancária 999 não foi encontrada"));
    }

    @Test
    public void deveRetornar201AoCriarContaBancariaValida() throws Exception {
        when(service.createContaBancaria(any(ContaBancariaRequestDTO.class))).thenReturn(responseDTO(1L));

        mockMvc.perform(post("/contasbancarias")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeContaBancaria").value("Conta Corrente"));
    }

    @Test
    public void deveRetornar400AoCriarContaBancariaComNomeEmBranco() throws Exception {
        ContaBancariaRequestDTO requestInvalido = new ContaBancariaRequestDTO(1L, "", "0001", "123456-7", BigDecimal.valueOf(1000));

        mockMvc.perform(post("/contasbancarias")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Erro de validação"));
    }

    @Test
    public void deveRetornar404AoCriarContaBancariaComBancoInexistente() throws Exception {
        when(service.createContaBancaria(any(ContaBancariaRequestDTO.class)))
                .thenThrow(new BancoNotFoundException(999L));

        mockMvc.perform(post("/contasbancarias")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar200AoAtualizarContaBancariaExistente() throws Exception {
        when(service.updateContaBancaria(eq(1L), any(ContaBancariaRequestDTO.class))).thenReturn(responseDTO(1L));

        mockMvc.perform(put("/contasbancarias/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeContaBancaria").value("Conta Corrente"));
    }

    @Test
    public void deveRetornar404AoAtualizarContaBancariaInexistente() throws Exception {
        when(service.updateContaBancaria(eq(999L), any(ContaBancariaRequestDTO.class)))
                .thenThrow(new ContaBancariaNotFoundException(999L));

        mockMvc.perform(put("/contasbancarias/{id}", 999L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar204AoDeletarContaBancariaExistente() throws Exception {
        doNothing().when(service).deleteContaBancaria(1L);

        mockMvc.perform(delete("/contasbancarias/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service).deleteContaBancaria(1L);
    }

    @Test
    public void deveRetornar404AoDeletarContaBancariaInexistente() throws Exception {
        doThrow(new ContaBancariaNotFoundException(999L)).when(service).deleteContaBancaria(999L);

        mockMvc.perform(delete("/contasbancarias/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deveRetornar409AoDeletarContaBancariaEmUso() throws Exception {
        doThrow(new ContaBancariaEmUsoException(1L)).when(service).deleteContaBancaria(1L);

        mockMvc.perform(delete("/contasbancarias/{id}", 1L))
                .andExpect(status().isConflict());
    }
}
