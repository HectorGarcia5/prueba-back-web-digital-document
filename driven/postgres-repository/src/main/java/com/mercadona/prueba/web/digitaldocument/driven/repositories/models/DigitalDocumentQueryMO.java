package com.mercadona.prueba.web.digitaldocument.driven.repositories.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Read-only projection of digital_document for the WEB micro. */
@Entity
@Immutable
@Table(name = "digital_document")
@Getter
@NoArgsConstructor
public class DigitalDocumentQueryMO {

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "employee_id")
  private String employeeId;

  @Column(name = "managed_group_id")
  private String managedGroupId;

  @Column(name = "status")
  private String status;

  @Column(name = "failed_step")
  private String failedStep;

  @Column(name = "storage_key")
  private String storageKey;

  @Column(name = "checksum")
  private String checksum;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;

  @Column(name = "published_at")
  private OffsetDateTime publishedAt;
}
