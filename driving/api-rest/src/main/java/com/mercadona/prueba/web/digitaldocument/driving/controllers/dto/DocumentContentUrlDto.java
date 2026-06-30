package com.mercadona.prueba.web.digitaldocument.driving.controllers.dto;

import lombok.Builder;
import lombok.Value;

/**
 * Response DTO for the document content URL endpoint.
 * The client should redirect to {@code contentUrl} to download or visualize the PDF.
 */
@Value
@Builder
public class DocumentContentUrlDto {

  /** Pre-signed URL granting temporary read access to the PDF. */
  String contentUrl;

  /** Remaining validity of the signed URL, in seconds. */
  long expiresInSeconds;
}
