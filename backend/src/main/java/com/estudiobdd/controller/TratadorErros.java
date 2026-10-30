package com.estudiobdd.controller;

import com.estudiobdd.service.AcessoNegadoException;
import com.estudiobdd.service.ConflitoException;
import com.estudiobdd.service.RecursoNaoEncontradoException;
import com.estudiobdd.service.RegraNegocioException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converte as exceções em respostas no formato ProblemDetail (RFC 9457):
 * { "status": 422, "title": "...", "detail": "..." }
 */
@RestControllerAdvice
public class TratadorErros {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail naoEncontrado(RecursoNaoEncontradoException e) {
        return problema(HttpStatus.NOT_FOUND, "Recurso não encontrado", e.getMessage());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ProblemDetail regraNegocio(RegraNegocioException e) {
        return problema(HttpStatus.UNPROCESSABLE_ENTITY, "Regra de negócio violada", e.getMessage());
    }

    @ExceptionHandler(ConflitoException.class)
    public ProblemDetail conflito(ConflitoException e) {
        return problema(HttpStatus.CONFLICT, "Conflito", e.getMessage());
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ProblemDetail acessoNegado(AcessoNegadoException e) {
        return problema(HttpStatus.FORBIDDEN, "Acesso negado", e.getMessage());
    }

    // falhas do Bean Validation nos DTOs: devolve a lista de campos com erro
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail dadosInvalidos(MethodArgumentNotValidException e) {
        Map<String, String> erros = new LinkedHashMap<>();
        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            erros.putIfAbsent(f.getField(), f.getDefaultMessage());
        }
        ProblemDetail pd = problema(HttpStatus.BAD_REQUEST, "Dados inválidos", "Um ou mais campos estão inválidos");
        pd.setProperty("erros", erros);
        return pd;
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ProblemDetail semCabecalho(MissingRequestHeaderException e) {
        return problema(HttpStatus.BAD_REQUEST, "Cabeçalho obrigatório",
                "Informe o usuário atual no cabeçalho " + e.getHeaderName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail corpoInvalido(HttpMessageNotReadableException e) {
        return problema(HttpStatus.BAD_REQUEST, "Requisição inválida",
                "O corpo da requisição está mal formatado ou tem um valor inválido (confira os enums)");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail parametroInvalido(MethodArgumentTypeMismatchException e) {
        return problema(HttpStatus.BAD_REQUEST, "Parâmetro inválido",
                "Valor inválido para o parâmetro " + e.getName());
    }

    // última barreira: restrição do banco que escapou da validação do serviço
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integridade(DataIntegrityViolationException e) {
        return problema(HttpStatus.CONFLICT, "Conflito de dados",
                "A operação viola uma restrição do banco (valor repetido ou registro em uso)");
    }

    private ProblemDetail problema(HttpStatus status, String titulo, String detalhe) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detalhe);
        pd.setTitle(titulo);
        return pd;
    }
}
