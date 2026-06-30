package com.mercadona.prueba.web.digitaldocument.application.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DigitalDocumentView(
    UUID id,
    String employeeId,
    String managedGroupId,
    String status,
    String failedStep,
    String storageKey,
    String checksum,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    OffsetDateTime publishedAt
) {}
