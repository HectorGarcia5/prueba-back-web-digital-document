package com.mercadona.prueba.web.digitaldocument.driven.repositories.adapters;

import com.mercadona.prueba.web.digitaldocument.application.model.DigitalDocumentView;
import com.mercadona.prueba.web.digitaldocument.application.model.DocumentPage;
import com.mercadona.prueba.web.digitaldocument.application.ports.driven.DocumentQueryRepository;
import com.mercadona.prueba.web.digitaldocument.driven.repositories.DigitalDocumentQueryJpaRepository;
import com.mercadona.prueba.web.digitaldocument.driven.repositories.mappers.DocumentMOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA adapter that implements {@link DocumentQueryRepository} using Spring Data JPA.
 */
@Component
@RequiredArgsConstructor
public class DocumentQueryRepositoryAdapter implements DocumentQueryRepository {

  private final DigitalDocumentQueryJpaRepository jpaRepository;
  private final DocumentMOMapper mapper;

  @Override
  @Transactional(readOnly = true)
  public Optional<DigitalDocumentView> findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toView);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<DigitalDocumentView> findByEmployeeIdAndManagedGroupId(String employeeId, String managedGroupId) {
    return jpaRepository.findByEmployeeIdAndManagedGroupId(employeeId, managedGroupId).map(mapper::toView);
  }

  @Override
  @Transactional(readOnly = true)
  public DocumentPage findByStatus(String status, int page, int size) {
    var moPage = jpaRepository.findByStatus(status, PageRequest.of(page, size));
    return new DocumentPage(
        moPage.getContent().stream().map(mapper::toView).toList(),
        moPage.getTotalElements(), page, size);
  }
}
