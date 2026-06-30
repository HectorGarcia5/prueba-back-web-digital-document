package com.mercadona.prueba.web.digitaldocument.application.exception;

import java.util.UUID;

public class DocumentNotFoundException extends RuntimeException {

  public DocumentNotFoundException(UUID documentId) {
    super("Document not found: " + documentId);
  }
}
