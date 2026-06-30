package com.mercadona.prueba.web.digitaldocument.driving.controllers.adapters;

import com.mercadona.prueba.web.digitaldocument.application.usecases.DocumentQueryUseCase;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.dto.DigitalDocumentResponseDto;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.dto.DocumentPageResponseDto;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.dto.DocumentStatusResponseDto;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.mappers.DocumentDTOMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.MDC;

import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping
public class DigitalDocumentControllerAdapter {

  private final DocumentQueryUseCase queryUseCase;
  private final DocumentDTOMapper mapper;

  @GetMapping("/api/v1/employees/{employeeId}/managed-groups/{managedGroupId}/document")
  public ResponseEntity<DigitalDocumentResponseDto> getDocumentByEmployee(
      @PathVariable String employeeId,
      @PathVariable String managedGroupId) {

    MDC.put("employeeId", employeeId);
    MDC.put("managedGroupId", managedGroupId);
    try {
      return queryUseCase.findByEmployee(employeeId, managedGroupId)
          .map(mapper::toResponseDto)
          .map(ResponseEntity::ok)
          .orElse(ResponseEntity.notFound().build());
    } finally {
      MDC.clear();
    }
  }

  @GetMapping("/api/v1/documents/{documentId}")
  public ResponseEntity<DigitalDocumentResponseDto> getDocumentById(
      @PathVariable UUID documentId) {

    MDC.put("documentId", documentId.toString());
    try {
      return queryUseCase.findById(documentId)
          .map(mapper::toResponseDto)
          .map(ResponseEntity::ok)
          .orElse(ResponseEntity.notFound().build());
    } finally {
      MDC.clear();
    }
  }

  @GetMapping("/api/v1/documents/{documentId}/status")
  public ResponseEntity<DocumentStatusResponseDto> getDocumentStatus(
      @PathVariable UUID documentId) {

    return queryUseCase.findById(documentId)
        .map(mapper::toStatusDto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/api/v1/documents")
  public ResponseEntity<DocumentPageResponseDto> listDocuments(
      @RequestParam String status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {

    var result = queryUseCase.findByStatus(status, page, size);
    return ResponseEntity.ok(mapper.toPageDto(result));
  }
}
