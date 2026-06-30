package com.mercadona.prueba.web.digitaldocument.application.exception;

import java.util.UUID;

public class DocumentNotRetryableException extends RuntimeException {

  public DocumentNotRetryableException(UUID documentId, String currentStatus) {
    super("Document " + documentId + " cannot be retried from status " + currentStatus);
  }
}
