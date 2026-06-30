package com.mercadona.prueba.web.digitaldocument.application.usecases;

import com.mercadona.prueba.web.digitaldocument.application.ports.driven.DocumentRepublishPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RepublishDocumentUseCaseTest {

    @Mock
    private DocumentRepublishPort republishPort;

    @InjectMocks
    private RepublishDocumentUseCase useCase;

    @Test
    void republish_delegatesToPortWithExactArguments() {
        var documentId = UUID.randomUUID();
        var employeeId = "EMP42";
        var managedGroupId = "MG99";

        useCase.republish(documentId, employeeId, managedGroupId);

        verify(republishPort).republish(documentId, employeeId, managedGroupId);
    }
}
