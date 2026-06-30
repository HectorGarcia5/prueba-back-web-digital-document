package com.mercadona.prueba.web.digitaldocument.application.ports.driven;

import java.util.UUID;

public interface DocumentRetryPort {

  void resetForRetry(UUID documentId);
}
