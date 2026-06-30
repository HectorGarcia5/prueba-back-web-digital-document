package com.mercadona.prueba.web.digitaldocument.application.ports.driven;

import com.mercadona.prueba.web.digitaldocument.application.model.DigitalDocumentView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentQueryRepository {

  Optional<DigitalDocumentView> findById(UUID id);

  Optional<DigitalDocumentView> findByEmployeeIdAndManagedGroupId(String employeeId, String managedGroupId);

  List<DigitalDocumentView> findByStatus(String status, int page, int size);

  long countByStatus(String status);
}
