package com.mercadona.prueba.web.digitaldocument.driven.repositories.adapters;

import com.mercadona.framework.cna.lib.bucket.service.BucketService;
import com.mercadona.prueba.web.digitaldocument.application.model.DocumentContentUrl;
import com.mercadona.prueba.web.digitaldocument.application.ports.driven.DocumentStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Driven adapter that generates signed URLs for PDF access via the configured object storage bucket.
 */
@Service
public class DocumentStoragePortAdapter implements DocumentStoragePort {

  private static final long SIGNED_URL_EXPIRATION_SECONDS = 120L;

  private final BucketService bucketService;
  private final String bucketId;

  /**
   * Creates the adapter.
   *
   * @param bucketService the FWK bucket service
   * @param bucketId      the logical bucket id from fwkcna.buckets[0].id — used to look up the client config
   */
  public DocumentStoragePortAdapter(
      BucketService bucketService,
      @Value("${fwkcna.buckets[0].id}") String bucketId) {
    this.bucketService = bucketService;
    this.bucketId = bucketId;
  }

  @Override
  public DocumentContentUrl getSignedUrl(String storageKey) {
    try {
      var uri = bucketService.getSignedUrl(bucketId, storageKey, SIGNED_URL_EXPIRATION_SECONDS);
      return new DocumentContentUrl(uri, SIGNED_URL_EXPIRATION_SECONDS);
    } catch (Exception e) {
      throw new RuntimeException(e); // TODO: Replace with corresponding business exception
    }
  }
}
