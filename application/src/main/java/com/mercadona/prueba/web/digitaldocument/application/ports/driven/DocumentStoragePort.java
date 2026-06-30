package com.mercadona.prueba.web.digitaldocument.application.ports.driven;

import com.mercadona.prueba.web.digitaldocument.application.model.DocumentContentUrl;

/**
 * Driven port for accessing stored document files in the object storage backend.
 */
public interface DocumentStoragePort {

  /**
   * Generates a time-limited signed URL for reading the PDF identified by the given storage key.
   *
   * @param storageKey the bucket path/key of the stored PDF
   * @return a signed URL and its expiration metadata
   */
  DocumentContentUrl getSignedUrl(String storageKey);
}
