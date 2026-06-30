package com.mercadona.prueba.web.digitaldocument.application.usecases;

import com.mercadona.framework.cna.commons.domain.MercadonaBusinessException;
import com.mercadona.prueba.web.digitaldocument.application.exception.DocumentNotFoundException;
import com.mercadona.prueba.web.digitaldocument.application.model.DocumentContentUrl;
import com.mercadona.prueba.web.digitaldocument.application.ports.driven.DocumentQueryRepository;
import com.mercadona.prueba.web.digitaldocument.application.ports.driven.DocumentStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Use case that generates a signed URL for accessing the PDF of a stored document.
 */
@Service
@RequiredArgsConstructor
public class GetDocumentContentUseCase {

  private final DocumentQueryRepository queryRepository;
  private final DocumentStoragePort storagePort;

  /**
   * Returns a time-limited signed URL to access the PDF of the document with the given ID.
   *
   * @param documentId the document identifier
   * @return the signed URL and its expiration metadata
   * @throws DocumentNotFoundException    if the document does not exist
   * @throws MercadonaBusinessException   if the document has not been stored yet
   */
  public DocumentContentUrl getContentUrl(UUID documentId) {
    var document = queryRepository.findById(documentId)
        .orElseThrow(() -> new DocumentNotFoundException(documentId));

    if (document.storageKey() == null) {
      throw new MercadonaBusinessException(
          "Document PDF is not available yet — current status: " + document.status(),
          "DOCUMENT_NOT_STORED");
    }

    return storagePort.getSignedUrl(document.storageKey());
  }
}
