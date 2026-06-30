package com.mercadona.prueba.web.digitaldocument.application.ports.driven;

import java.util.UUID;

/**
 * Driven port for triggering republication of a digital document event to Kafka.
 */
public interface DocumentRepublishPort {

  /**
   * Publishes a republication event for the given document.
   *
   * @param documentId     the document identifier
   * @param employeeId     the employee identifier
   * @param managedGroupId the managed group identifier
   */
  void republish(UUID documentId, String employeeId, String managedGroupId);
}
