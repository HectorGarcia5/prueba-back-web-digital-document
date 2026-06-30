package com.mercadona.prueba.web.digitaldocument.driving.controllers.adapters;

import com.mercadona.prueba.web.digitaldocument.application.usecases.RepublishDocumentUseCase;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.api.UtilsApi;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.dto.RepublishRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/**
 * Internal endpoint called by the BTC micro to schedule republication.
 * Implements the contract defined in {@link UtilsApi}.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class DocumentRepublishController implements UtilsApi {

  private final RepublishDocumentUseCase republishUseCase;

  @Override
  @PreAuthorize("hasRole('DIGITAL_DOCUMENT_REPUBLISH')")
  public ResponseEntity<Void> republish(RepublishRequest request) {
    log.info("event=REPUBLISH_REQUEST documentId={}", request.getDocumentId());
    republishUseCase.republish(request.getDocumentId(), request.getEmployeeId(), request.getManagedGroupId());
    return ResponseEntity.accepted().build();
  }
}
