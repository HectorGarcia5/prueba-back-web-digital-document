package com.mercadona.prueba.web.digitaldocument.driving.controllers.dto;

import lombok.Builder;
import lombok.Value;

/**
 * Standard error response body returned by {@link
 * com.mercadona.prueba.web.digitaldocument.driving.controllers.error.DigitalDocumentControllerAdvice}.
 */
@Value
@Builder
public class ErrorResponseDto {

  ErrorDetail error;

  /**
   * Creates an error response with the given code and description.
   *
   * @param code        machine-readable error code
   * @param description human-readable description
   * @return the error response DTO
   */
  public static ErrorResponseDto of(String code, String description) {
    return ErrorResponseDto.builder()
        .error(ErrorDetail.builder().code(code).description(description).build())
        .build();
  }

  /** Inner detail object. */
  @Value
  @Builder
  public static class ErrorDetail {
    String code;
    String description;
  }
}
