package com.gustavolopes.money_pilot.advice;

import com.gustavolopes.money_pilot.dto.ErroRespostaDTO;
import com.gustavolopes.money_pilot.exception.CategoriaEmUsoException;
import com.gustavolopes.money_pilot.exception.CategoriaNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CategoriaNotFoundException.class)
    public ResponseEntity<ErroRespostaDTO> handleCategoriaNotFound(CategoriaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroRespostaDTO(LocalDateTime.now(), 404, ex.getMessage(), null));
    }

    @ExceptionHandler(CategoriaEmUsoException.class)
    public ResponseEntity<ErroRespostaDTO> handleCategoriaEmUso(CategoriaEmUsoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErroRespostaDTO(LocalDateTime.now(), 409, ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> handleValidacao(MethodArgumentNotValidException ex) {
        List<String> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest()
                .body(new ErroRespostaDTO(LocalDateTime.now(), 400, "Erro de validação", erros));
    }
}
