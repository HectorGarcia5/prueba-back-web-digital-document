package com.mercadona.prueba.web.digitaldocument.driven.repositories;

import com.mercadona.prueba.web.digitaldocument.driven.repositories.models.DigitalDocumentQueryMO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DigitalDocumentQueryJpaRepository extends JpaRepository<DigitalDocumentQueryMO, UUID> {

  Optional<DigitalDocumentQueryMO> findByEmployeeIdAndManagedGroupId(String employeeId, String managedGroupId);

  Page<DigitalDocumentQueryMO> findByStatus(String status, Pageable pageable);

  @Modifying
  @Query(value = """
      UPDATE digital_document
         SET retry_count   = 0,
             next_retry_at = NULL,
             updated_at    = NOW()
       WHERE id = :id
      """, nativeQuery = true)
  void resetForRetry(@Param("id") UUID id);
}
