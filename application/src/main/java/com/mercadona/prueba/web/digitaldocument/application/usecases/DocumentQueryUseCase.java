package com.mercadona.prueba.web.digitaldocument.application.usecases;

import com.mercadona.prueba.web.digitaldocument.application.model.DigitalDocumentView;
import com.mercadona.prueba.web.digitaldocument.application.model.DocumentPage;
import com.mercadona.prueba.web.digitaldocument.application.ports.driven.DocumentQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentQueryUseCase {

  private final DocumentQueryRepository repository;

  public Optional<DigitalDocumentView> findById(UUID id) {
    return repository.findById(id);
  }

  public Optional<DigitalDocumentView> findByEmployee(String employeeId, String managedGroupId) {
    return repository.findByEmployeeIdAndManagedGroupId(employeeId, managedGroupId);
  }

  public DocumentPage findByStatus(String status, int page, int size) {
    return repository.findByStatus(status, page, size);
  }
}
