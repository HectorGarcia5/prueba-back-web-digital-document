package com.mercadona.prueba.web.digitaldocument.application.usecases;

import com.mercadona.prueba.web.digitaldocument.application.exception.DocumentNotFoundException;
import com.mercadona.prueba.web.digitaldocument.application.exception.DocumentNotRetryableException;
import com.mercadona.prueba.web.digitaldocument.application.ports.driven.DocumentQueryRepository;
import com.mercadona.prueba.web.digitaldocument.application.ports.driven.DocumentRetryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryDocumentUseCase {

  private final DocumentQueryRepository queryRepository;
  private final DocumentRetryPort retryPort;

  public void retry(UUID documentId) {
    var document = queryRepository.findById(documentId)
        .orElseThrow(() -> new DocumentNotFoundException(documentId));

    if (!"FAILED".equals(document.status())) {
      throw new DocumentNotRetryableException(documentId, document.status());
    }

    retryPort.resetForRetry(documentId);
    log.info("event=RETRY_REQUESTED documentId={}", documentId);
  }
}
