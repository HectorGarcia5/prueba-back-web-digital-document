package com.mercadona.prueba.web.digitaldocument.driving.controllers.adapters;

import com.mercadona.prueba.web.digitaldocument.driving.controllers.dto.RepublishRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Internal endpoint called by the BTC micro to schedule republication of a document.
 * Inserts a MANUAL_RETRY Outbox event — the SNK OutboxPublisher delivers it to Kafka.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/utils/documents")
@RequiredArgsConstructor
public class DocumentRepublishController {

  private static final String TOPIC =
      "thirdparty.employee.employeedigitaldocument.event.restrictedout.v0.table.cpd";
  private static final String EVENT_TYPE = "EmployeeDigitalDocumentCreated";

  private final JdbcTemplate jdbcTemplate;

  @PostMapping("/republish")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void republish(@Valid @RequestBody RepublishRequest request) {
    log.info("event=REPUBLISH_REQUEST documentId={}", request.getDocumentId());

    String payload = "{\"digitalDocumentId\":\"" + request.getDocumentId()
        + "\",\"employeeId\":\"" + request.getEmployeeId()
        + "\",\"managedGroupId\":\"" + request.getManagedGroupId() + "\"}";

    jdbcTemplate.update("""
        INSERT INTO outbox_event
          (id, aggregate_id, event_type, topic, event_key, payload,
           status, publication_reason, attempts, created_at)
        VALUES (?, ?, ?, ?, ?, ?, 'PENDING', 'MANUAL_RETRY', 0, ?)
        """,
        UUID.randomUUID(),
        request.getDocumentId(),
        EVENT_TYPE,
        TOPIC,
        request.getDocumentId().toString(),
        payload,
        OffsetDateTime.now()
    );

    log.info("event=REPUBLISH_OUTBOX_CREATED documentId={}", request.getDocumentId());
  }
}
