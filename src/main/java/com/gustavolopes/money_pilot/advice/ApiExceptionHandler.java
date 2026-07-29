package com.gustavolopes.money_pilot.advice;

import com.gustavolopes.money_pilot.dto.ErroRespostaDTO;
import com.gustavolopes.money_pilot.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
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

    @ExceptionHandler(ParceiroNotFoundException.class)
    public ResponseEntity<ErroRespostaDTO> handleParceiroNotFound(ParceiroNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroRespostaDTO(LocalDateTime.now(), 404, ex.getMessage(), null));
    }

    @ExceptionHandler(ParceiroEmUsoException.class)
    public ResponseEntity<ErroRespostaDTO> handleParceiroEmUso(ParceiroEmUsoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErroRespostaDTO(LocalDateTime.now(), 409, ex.getMessage(), null));
    }

    @ExceptionHandler(DocumentoImutavelException.class)
    public ResponseEntity<ErroRespostaDTO> handleDocumentoImutavel(DocumentoImutavelException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroRespostaDTO(LocalDateTime.now(), 400, ex.getMessage(), null));
    }

    @ExceptionHandler(DocumentoInvalidoException.class)
    public ResponseEntity<ErroRespostaDTO> handleDocumentoInvalido(DocumentoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroRespostaDTO(LocalDateTime.now(), 400, ex.getMessage(), null));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroRespostaDTO> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> erros = ex.getConstraintViolations().stream()
                .map(violacao -> violacao.getPropertyPath() + ": " + violacao.getMessage())
                .toList();

        return ResponseEntity.badRequest()
                .body(new ErroRespostaDTO(LocalDateTime.now(), 400, "Erro de validação", erros));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroRespostaDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErroRespostaDTO(LocalDateTime.now(), 409,
                        "Os dados informados violam uma restrição de integridade (ex: documento duplicado)", null));
    }

    @ExceptionHandler(ReceitaNotFoundException.class)
    public ResponseEntity<ErroRespostaDTO> handleReceitaNotFound(ReceitaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroRespostaDTO(LocalDateTime.now(), 404, ex.getMessage(), null));
    }

    @ExceptionHandler(DespesaNotFoundException.class)
    public ResponseEntity<ErroRespostaDTO> handleDespesaNotFound(DespesaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroRespostaDTO(LocalDateTime.now(), 404, ex.getMessage(), null));
    }

    @ExceptionHandler(BancoNotFoundException.class)
    public ResponseEntity<ErroRespostaDTO> handleBancoNotFound(BancoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroRespostaDTO(LocalDateTime.now(), 404, ex.getMessage(), null));
    }

    @ExceptionHandler(BancoEmUsoException.class)
    public ResponseEntity<ErroRespostaDTO> handleBancoEmUso(BancoEmUsoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErroRespostaDTO(LocalDateTime.now(), 409, ex.getMessage(), null));
    }

    @ExceptionHandler(BancoCodigoImutavelException.class)
    public ResponseEntity<ErroRespostaDTO> handleBancoCodigoImutavel(BancoCodigoImutavelException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroRespostaDTO(LocalDateTime.now(), 400, ex.getMessage(), null));
    }

    @ExceptionHandler(ContaBancariaNotFoundException.class)
    public ResponseEntity<ErroRespostaDTO> handleContaBancariaNotFound(ContaBancariaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroRespostaDTO(LocalDateTime.now(), 404, ex.getMessage(), null));
    }

    @ExceptionHandler(ContaBancariaEmUsoException.class)
    public ResponseEntity<ErroRespostaDTO> handleContaBancariaEmUso(ContaBancariaEmUsoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErroRespostaDTO(LocalDateTime.now(), 409, ex.getMessage(), null));
    }

    @ExceptionHandler(RecebimentoNotFoundException.class)
    public ResponseEntity<ErroRespostaDTO> handleRecebimentoNotFound(RecebimentoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroRespostaDTO(LocalDateTime.now(), 404, ex.getMessage(), null));
    }

    @ExceptionHandler(ReceitaNaoEncontradaNaListaException.class)
    public ResponseEntity<ErroRespostaDTO> handleReceitaNaoEncontradaNaLista(ReceitaNaoEncontradaNaListaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroRespostaDTO(LocalDateTime.now(), 404, ex.getMessage(), null));
    }

    @ExceptionHandler(RecebimentoNaoConfirmadoException.class)
    public ResponseEntity<ErroRespostaDTO> handleRecebimentoNaoConfirmado(RecebimentoNaoConfirmadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErroRespostaDTO(LocalDateTime.now(), 409, ex.getMessage(), null));
    }

    @ExceptionHandler(RecebimentoNaoEstornadoException.class)
    public ResponseEntity<ErroRespostaDTO> handleRecebimentoNaoEstornado(RecebimentoNaoEstornadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErroRespostaDTO(LocalDateTime.now(), 409, ex.getMessage(), null));
    }
}
