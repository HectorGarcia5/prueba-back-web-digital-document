package com.mercadona.prueba.web.digitaldocument.application.usecases;

import com.mercadona.prueba.web.digitaldocument.application.ports.driven.DocumentRepublishPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Use case that orchestrates the republication of a digital document event.
 */
@Service
@RequiredArgsConstructor
public class RepublishDocumentUseCase {

  private final DocumentRepublishPort republishPort;

  /**
   * Triggers republication of the document event identified by the given coordinates.
   *
   * @param documentId     the document identifier
   * @param employeeId     the employee identifier
   * @param managedGroupId the managed group identifier
   */
  public void republish(UUID documentId, String employeeId, String managedGroupId) {
    republishPort.republish(documentId, employeeId, managedGroupId);
  }
}
