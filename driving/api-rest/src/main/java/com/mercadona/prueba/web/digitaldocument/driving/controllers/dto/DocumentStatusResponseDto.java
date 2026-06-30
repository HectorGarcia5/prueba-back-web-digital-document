package com.mercadona.prueba.web.digitaldocument.driving.controllers.dto;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class DocumentStatusResponseDto {

  UUID documentId;
  String status;
  String failedStep;
  OffsetDateTime updatedAt;
}
