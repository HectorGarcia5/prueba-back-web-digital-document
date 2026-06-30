package com.mercadona.prueba.web.digitaldocument.application.model;

import java.net.URI;

/**
 * Represents a time-limited signed URL to access a stored document PDF.
 *
 * @param signedUrl        the pre-signed URL granting read access to the PDF
 * @param expiresInSeconds how many seconds the URL remains valid
 */
public record DocumentContentUrl(URI signedUrl, long expiresInSeconds) {}
