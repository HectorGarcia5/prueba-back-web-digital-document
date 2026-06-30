package com.mercadona.prueba.web.digitaldocument.driven.repositories.adapters;

import com.mercadona.framework.cna.lib.outbox.avro.jpa.register.service.OutBoxAvroJPAService;
import com.mercadona.prueba.web.digitaldocument.application.ports.driven.DocumentRepublishPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import thirdparty.employee.employeedigitaldocument.v0.DataRecord;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeDigitalDocument;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeDigitalDocumentEventRestrictedOutKey;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeDigitalDocumentEventRestrictedOutValue;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeDigitalDocumentsRecord;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeIds;
import thirdparty.employee.employeedigitaldocument.v0.ManagedGroupIds;

import java.util.List;
import java.util.UUID;

/**
 * Driven adapter that persists a republication event to the outbox table.
 * The FWK relay then publishes it to Kafka asynchronously.
 */
@Slf4j
@Component
public class DocumentRepublishPortAdapter implements DocumentRepublishPort {

  private final OutBoxAvroJPAService outBoxAvroJPAService;
  private final String outputTopic;

  /**
   * Creates the adapter.
   *
   * @param outBoxAvroJPAService the outbox service bound to the sr-basic schema registry
   * @param outputTopic          the target Kafka topic
   */
  public DocumentRepublishPortAdapter(
      @Qualifier("sr-basic") OutBoxAvroJPAService outBoxAvroJPAService,
      @Value("${outbox.topic.employee-digital-document}") String outputTopic) {
    this.outBoxAvroJPAService = outBoxAvroJPAService;
    this.outputTopic = outputTopic;
  }

  @Override
  public void republish(UUID documentId, String employeeId, String managedGroupId) {
    var key = buildKey(employeeId, managedGroupId);
    var value = buildValue(documentId, employeeId, managedGroupId);
    outBoxAvroJPAService.save(key, value, outputTopic);
    log.info("event=REPUBLISH_OUTBOX_SAVED documentId={}", documentId);
  }

  private EmployeeDigitalDocumentEventRestrictedOutKey buildKey(String employeeId, String managedGroupId) {
    return EmployeeDigitalDocumentEventRestrictedOutKey.newBuilder()
        .setEmployeeId(employeeId)
        .setManagedGroupId(managedGroupId)
        .build();
  }

  private EmployeeDigitalDocumentEventRestrictedOutValue buildValue(
      UUID documentId, String employeeId, String managedGroupId) {

    var managedGroupIds = ManagedGroupIds.newBuilder()
        .setId(managedGroupId)
        .build();

    var employeeIds = EmployeeIds.newBuilder()
        .setId(employeeId)
        .setManagedGroupId(managedGroupIds)
        .build();

    var docRecord = EmployeeDigitalDocumentsRecord.newBuilder()
        .setId(documentId.toString())
        .build();

    var dataRecord = DataRecord.newBuilder()
        .setEmployeeData(employeeIds)
        .setEmployeeDigitalDocuments(List.of(docRecord))
        .build();

    var payload = EmployeeDigitalDocument.newBuilder()
        .setData(dataRecord)
        .build();

    return EmployeeDigitalDocumentEventRestrictedOutValue.newBuilder()
        .setPayload(payload)
        .build();
  }
}
