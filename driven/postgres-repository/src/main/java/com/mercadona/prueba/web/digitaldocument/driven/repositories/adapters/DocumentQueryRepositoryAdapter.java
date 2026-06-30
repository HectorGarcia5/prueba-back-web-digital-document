package com.mercadona.prueba.web.digitaldocument.driven.repositories.adapters;

import com.mercadona.prueba.web.digitaldocument.application.model.DigitalDocumentView;
import com.mercadona.prueba.web.digitaldocument.application.ports.driven.DocumentQueryRepository;
import com.mercadona.prueba.web.digitaldocument.driven.repositories.DigitalDocumentQueryJpaRepository;
import com.mercadona.prueba.web.digitaldocument.driven.repositories.models.DigitalDocumentQueryMO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DocumentQueryRepositoryAdapter implements DocumentQueryRepository {

  private final DigitalDocumentQueryJpaRepository jpaRepository;

  @Override
  public Optional<DigitalDocumentView> findById(UUID id) {
    return jpaRepository.findById(id).map(this::toView);
  }

  @Override
  public Optional<DigitalDocumentView> findByEmployeeIdAndManagedGroupId(String employeeId, String managedGroupId) {
    return jpaRepository.findByEmployeeIdAndManagedGroupId(employeeId, managedGroupId).map(this::toView);
  }

  @Override
  public List<DigitalDocumentView> findByStatus(String status, int page, int size) {
    return jpaRepository.findByStatus(status, PageRequest.of(page, size))
        .stream().map(this::toView).toList();
  }

  @Override
  public long countByStatus(String status) {
    return jpaRepository.countByStatus(status);
  }

  private DigitalDocumentView toView(DigitalDocumentQueryMO mo) {
    return new DigitalDocumentView(
        mo.getId(), mo.getEmployeeId(), mo.getManagedGroupId(),
        mo.getStatus(), mo.getFailedStep(), mo.getStorageKey(),
        mo.getChecksum(), mo.getCreatedAt(), mo.getUpdatedAt(), mo.getPublishedAt()
    );
  }
}
