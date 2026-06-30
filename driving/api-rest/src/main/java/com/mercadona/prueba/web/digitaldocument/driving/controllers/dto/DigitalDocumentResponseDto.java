package com.mercadona.prueba.web.digitaldocument.driving.controllers.dto;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class DigitalDocumentResponseDto {

  UUID id;
  String employeeId;
  String managedGroupId;
  String status;
  String failedStep;
  String storageKey;
  String checksum;
  OffsetDateTime createdAt;
  OffsetDateTime updatedAt;
  OffsetDateTime publishedAt;
}
