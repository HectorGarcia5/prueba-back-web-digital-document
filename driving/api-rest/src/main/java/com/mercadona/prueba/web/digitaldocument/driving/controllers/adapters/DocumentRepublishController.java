package com.mercadona.prueba.web.digitaldocument.driving.controllers.adapters;

import com.mercadona.framework.cna.lib.outbox.avro.jpa.register.service.OutBoxAvroJPAService;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.dto.RepublishRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeDigitalDocumentEventRestrictedOutKey;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeDigitalDocumentEventRestrictedOutValue;

/**
 * Internal endpoint called by the BTC micro to schedule republication.
 * Saves an Outbox event via OutBoxAvroJPAService — the framework auto-publishes to Kafka.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/utils/documents")
@RequiredArgsConstructor
public class DocumentRepublishController {

  private final OutBoxAvroJPAService outBoxAvroJPAService;

  @Value("${outbox.topic.employee-digital-document}")
  private String outputTopic;

  @PostMapping("/republish")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void republish(@Valid @RequestBody RepublishRequest request) {
    log.info("event=REPUBLISH_REQUEST documentId={}", request.getDocumentId());

    var key = EmployeeDigitalDocumentEventRestrictedOutKey.newBuilder()
        .setEmployeeId(request.getEmployeeId())
        .setManagedGroupId(request.getManagedGroupId())
        .build();

    var value = EmployeeDigitalDocumentEventRestrictedOutValue.newBuilder()
        .setDigitalDocumentId(request.getDocumentId().toString())
        .setEmployeeId(request.getEmployeeId())
        .setManagedGroupId(request.getManagedGroupId())
        .build();

    outBoxAvroJPAService.save(key, value, outputTopic);
    log.info("event=REPUBLISH_OUTBOX_SAVED documentId={}", request.getDocumentId());
  }
}
