package com.mercadona.prueba.web.digitaldocument.driven.repositories.adapters;

import com.mercadona.framework.cna.lib.outbox.avro.jpa.register.service.OutBoxAvroJPAService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeDigitalDocumentEventRestrictedOutKey;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeDigitalDocumentEventRestrictedOutValue;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DocumentRepublishPortAdapterTest {

    private static final String TOPIC = "test.topic.v0";

    @Mock
    private OutBoxAvroJPAService outBoxAvroJPAService;

    private DocumentRepublishPortAdapter buildAdapter() {
        return new DocumentRepublishPortAdapter(outBoxAvroJPAService, TOPIC);
    }

    // -----------------------------------------------------------------------
    // republish — happy path
    // -----------------------------------------------------------------------

    @Test
    void republish_callsSaveWithCorrectKeyAndTopic() {
        var documentId = UUID.randomUUID();
        var employeeId = "EMP42";
        var managedGroupId = "MG99";
        var adapter = buildAdapter();

        ArgumentCaptor<EmployeeDigitalDocumentEventRestrictedOutKey> keyCaptor =
                forClass(EmployeeDigitalDocumentEventRestrictedOutKey.class);
        ArgumentCaptor<EmployeeDigitalDocumentEventRestrictedOutValue> valueCaptor =
                forClass(EmployeeDigitalDocumentEventRestrictedOutValue.class);
        ArgumentCaptor<String> topicCaptor = forClass(String.class);

        adapter.republish(documentId, employeeId, managedGroupId);

        verify(outBoxAvroJPAService).save(keyCaptor.capture(), valueCaptor.capture(), topicCaptor.capture());

        var key = keyCaptor.getValue();
        assertThat(key.getEmployeeId()).isEqualTo(employeeId);
        assertThat(key.getManagedGroupId()).isEqualTo(managedGroupId);

        assertThat(topicCaptor.getValue()).isEqualTo(TOPIC);
    }

    @Test
    void republish_callsSaveWithCorrectValuePayload() {
        var documentId = UUID.randomUUID();
        var employeeId = "EMP1";
        var managedGroupId = "MG1";
        var adapter = buildAdapter();

        ArgumentCaptor<EmployeeDigitalDocumentEventRestrictedOutValue> valueCaptor =
                forClass(EmployeeDigitalDocumentEventRestrictedOutValue.class);

        adapter.republish(documentId, employeeId, managedGroupId);

        verify(outBoxAvroJPAService).save(
                org.mockito.ArgumentMatchers.any(),
                valueCaptor.capture(),
                org.mockito.ArgumentMatchers.any());

        var value = valueCaptor.getValue();
        var data = value.getPayload().getData();

        assertThat(data.getEmployeeData().getId()).isEqualTo(employeeId);
        assertThat(data.getEmployeeData().getManagedGroupId().getId()).isEqualTo(managedGroupId);
        assertThat(data.getEmployeeDigitalDocuments()).hasSize(1);
        assertThat(data.getEmployeeDigitalDocuments().get(0).getId()).isEqualTo(documentId.toString());
    }
}
