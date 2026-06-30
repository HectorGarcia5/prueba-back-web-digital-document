package com.mercadona.prueba.web.digitaldocument.driven.repositories;

import com.mercadona.prueba.web.digitaldocument.driven.repositories.models.DigitalDocumentQueryMO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DigitalDocumentQueryJpaRepository extends JpaRepository<DigitalDocumentQueryMO, UUID> {

  Optional<DigitalDocumentQueryMO> findByEmployeeIdAndManagedGroupId(String employeeId, String managedGroupId);

  List<DigitalDocumentQueryMO> findByStatus(String status, Pageable pageable);

  long countByStatus(String status);
}
