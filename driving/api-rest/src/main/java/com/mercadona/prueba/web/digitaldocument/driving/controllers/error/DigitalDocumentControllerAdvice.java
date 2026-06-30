package com.mercadona.prueba.web.digitaldocument.driving.controllers.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class DigitalDocumentControllerAdvice {

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException e) {
    return ResponseEntity.badRequest()
        .body(error("MISSING_PARAMETER", "Required parameter '" + e.getParameterName() + "' is missing"));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
    return ResponseEntity.badRequest()
        .body(error("INVALID_PARAMETER", "Invalid value for parameter '" + e.getName() + "'"));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
    log.warn("Bad request: {}", e.getMessage());
    return ResponseEntity.badRequest()
        .body(error("BAD_REQUEST", e.getMessage()));
  }

  private Map<String, Object> error(String code, String description) {
    return Map.of("error", Map.of("code", code, "description", description));
  }
}
