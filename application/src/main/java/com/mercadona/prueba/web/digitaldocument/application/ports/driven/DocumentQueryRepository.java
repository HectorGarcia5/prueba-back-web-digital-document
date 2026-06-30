package com.mercadona.prueba.web.digitaldocument.application.ports.driven;

import com.mercadona.prueba.web.digitaldocument.application.model.DigitalDocumentView;
import com.mercadona.prueba.web.digitaldocument.application.model.DocumentPage;

import java.util.Optional;
import java.util.UUID;

/**
 * Driven port for querying digital documents.
 */
public interface DocumentQueryRepository {

  /**
   * Finds a document by its identifier.
   *
   * @param id the document UUID
   * @return the document view, or empty if not found
   */
  Optional<DigitalDocumentView> findById(UUID id);

  /**
   * Finds the document for a given employee and managed group.
   *
   * @param employeeId     the employee identifier
   * @param managedGroupId the managed group identifier
   * @return the document view, or empty if not found
   */
  Optional<DigitalDocumentView> findByEmployeeIdAndManagedGroupId(String employeeId, String managedGroupId);

  /**
   * Returns a page of documents filtered by status.
   *
   * @param status the document status filter
   * @param page   zero-based page index
   * @param size   page size
   * @return paginated result including total count
   */
  DocumentPage findByStatus(String status, int page, int size);
}
