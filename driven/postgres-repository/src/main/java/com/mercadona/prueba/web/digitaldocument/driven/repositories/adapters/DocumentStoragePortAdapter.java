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
  private final String bucketName;

  /**
   * Creates the adapter.
   *
   * @param bucketService the FWK bucket service
   * @param bucketName    the configured bucket name read from fwkcna.buckets[0].bucket-name
   */
  public DocumentStoragePortAdapter(
      BucketService bucketService,
      @Value("${fwkcna.buckets[0].bucket-name}") String bucketName) {
    this.bucketService = bucketService;
    this.bucketName = bucketName;
  }

  @Override
  public DocumentContentUrl getSignedUrl(String storageKey) {
    try {
      var uri = bucketService.getSignedUrl(bucketName, storageKey, SIGNED_URL_EXPIRATION_SECONDS);
      return new DocumentContentUrl(uri, SIGNED_URL_EXPIRATION_SECONDS);
    } catch (Exception e) {
      throw new RuntimeException(e); // TODO: Replace with corresponding business exception
    }
  }
}
