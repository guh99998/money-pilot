package com.gustavolopes.money_pilot.advice;

import com.gustavolopes.money_pilot.dto.ErroRespostaDTO;
import com.gustavolopes.money_pilot.exception.CategoriaEmUsoException;
import com.gustavolopes.money_pilot.exception.CategoriaNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiExceptionHandlerTest {

    ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    public void deveMapearCategoriaNotFoundExceptionPara404() {
        CategoriaNotFoundException ex = new CategoriaNotFoundException(999L);

        ResponseEntity<ErroRespostaDTO> resposta = handler.handleCategoriaNotFound(ex);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody().status()).isEqualTo(404);
        assertThat(resposta.getBody().mensagem()).isEqualTo("A categoria 999 não foi encontrada");
    }

    @Test
    public void deveMapearCategoriaEmUsoExceptionPara409() {
        CategoriaEmUsoException ex = new CategoriaEmUsoException(1L);

        ResponseEntity<ErroRespostaDTO> resposta = handler.handleCategoriaEmUso(ex);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resposta.getBody().status()).isEqualTo(409);
        assertThat(resposta.getBody().mensagem()).contains("não pode ser removida");
    }

    @Test
    public void deveMapearErrosDeValidacaoPara400ComListaDeErros() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "categoriaRequestDTO");
        bindingResult.addError(new FieldError("categoriaRequestDTO", "nome", "O nome da categoria é obrigatório"));
        bindingResult.addError(new FieldError("categoriaRequestDTO", "tagCor", "A cor da tag é obrigatória"));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErroRespostaDTO> resposta = handler.handleValidacao(ex);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody().mensagem()).isEqualTo("Erro de validação");
        assertThat(resposta.getBody().erros())
                .containsExactlyInAnyOrder(
                        "nome: O nome da categoria é obrigatório",
                        "tagCor: A cor da tag é obrigatória"
                );
    }
}
