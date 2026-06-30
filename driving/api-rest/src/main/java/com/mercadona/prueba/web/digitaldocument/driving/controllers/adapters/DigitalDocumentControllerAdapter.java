package com.mercadona.prueba.web.digitaldocument.driving.controllers.adapters;

import com.mercadona.prueba.web.digitaldocument.application.usecases.DocumentQueryUseCase;
import com.mercadona.prueba.web.digitaldocument.application.usecases.GetDocumentContentUseCase;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.api.DigitalDocumentApi;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.dto.DigitalDocumentResponseDto;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.dto.DocumentPageResponseDto;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.dto.DocumentStatusResponseDto;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.mappers.DocumentDTOMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller adapter for digital document query operations.
 * Implements the contract defined in {@link DigitalDocumentApi}.
 * MDC context is populated by {@link
 * com.mercadona.prueba.web.digitaldocument.driving.controllers.filter.MdcRequestContextFilter}.
 */
@Slf4j
@RestController
@AllArgsConstructor
public class DigitalDocumentControllerAdapter implements DigitalDocumentApi {

  private final DocumentQueryUseCase queryUseCase;
  private final GetDocumentContentUseCase getDocumentContentUseCase;
  private final DocumentDTOMapper mapper;

  @Override
  @PreAuthorize("hasRole('DIGITAL_DOCUMENT_READ')")
  public ResponseEntity<DigitalDocumentResponseDto> getDocumentByEmployee(
      String employeeId, String managedGroupId) {

    return queryUseCase.findByEmployee(employeeId, managedGroupId)
        .map(mapper::toResponseDto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @Override
  @PreAuthorize("hasRole('DIGITAL_DOCUMENT_READ')")
  public ResponseEntity<DigitalDocumentResponseDto> getDocumentById(UUID documentId) {
    return queryUseCase.findById(documentId)
        .map(mapper::toResponseDto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @Override
  @PreAuthorize("hasRole('DIGITAL_DOCUMENT_READ')")
  public ResponseEntity<DocumentStatusResponseDto> getDocumentStatus(UUID documentId) {
    return queryUseCase.findById(documentId)
        .map(mapper::toStatusDto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @Override
  @PreAuthorize("hasRole('DIGITAL_DOCUMENT_READ')")
  public ResponseEntity<DocumentPageResponseDto> listDocumentsByStatus(
      String status, Integer page, Integer size) {

    int pageVal = page != null ? page : 0;
    int sizeVal = size != null ? size : 20;
    return ResponseEntity.ok(mapper.toPageDto(queryUseCase.findByStatus(status, pageVal, sizeVal)));
  }

  @Override
  @PreAuthorize("hasRole('DIGITAL_DOCUMENT_READ')")
  public ResponseEntity<Void> getDocumentContent(UUID documentId) {
    var contentUrl = getDocumentContentUseCase.getContentUrl(documentId);
    return ResponseEntity.status(302).location(contentUrl.signedUrl()).build();
  }

}
