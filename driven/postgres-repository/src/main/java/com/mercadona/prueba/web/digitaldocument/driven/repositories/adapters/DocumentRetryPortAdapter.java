package com.mercadona.prueba.web.digitaldocument.driven.repositories.adapters;

import com.mercadona.prueba.web.digitaldocument.application.ports.driven.DocumentRetryPort;
import com.mercadona.prueba.web.digitaldocument.driven.repositories.DigitalDocumentQueryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DocumentRetryPortAdapter implements DocumentRetryPort {

  private final DigitalDocumentQueryJpaRepository jpaRepository;

  @Override
  @Transactional
  public void resetForRetry(UUID documentId) {
    jpaRepository.resetForRetry(documentId);
  }
}
