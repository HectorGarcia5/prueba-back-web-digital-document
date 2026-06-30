package com.mercadona.prueba.web.digitaldocument.driving.controllers.error;

import com.mercadona.framework.cna.commons.domain.MercadonaBusinessException;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class DigitalDocumentControllerAdvice {

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponseDto> handleMissingParam(MissingServletRequestParameterException e) {
    return ResponseEntity.badRequest()
        .body(ErrorResponseDto.of("MISSING_PARAMETER", "Required parameter '" + e.getParameterName() + "' is missing"));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponseDto> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
    return ResponseEntity.badRequest()
        .body(ErrorResponseDto.of("INVALID_PARAMETER", "Invalid value for parameter '" + e.getName() + "'"));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException e) {
    String details = e.getBindingResult().getFieldErrors().stream()
        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
        .collect(Collectors.joining(", "));
    return ResponseEntity.badRequest()
        .body(ErrorResponseDto.of("VALIDATION_ERROR", details));
  }

  @ExceptionHandler(MercadonaBusinessException.class)
  public ResponseEntity<ErrorResponseDto> handleBusinessException(MercadonaBusinessException e) {
    log.warn("event=BUSINESS_EXCEPTION code={} message={}", e.getErrorCode(), e.getMessage());
    return ResponseEntity.unprocessableEntity()
        .body(ErrorResponseDto.of(e.getErrorCode(), e.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException e) {
    log.warn("event=BAD_REQUEST message={}", e.getMessage());
    return ResponseEntity.badRequest()
        .body(ErrorResponseDto.of("BAD_REQUEST", e.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleUnexpected(Exception e) {
    log.error("event=UNEXPECTED_ERROR message={}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponseDto.of("INTERNAL_ERROR", "Internal server error"));
  }
}
