package com.mercadona.prueba.web.digitaldocument.driven.repositories.adapters;

import com.mercadona.prueba.web.digitaldocument.driven.repositories.DigitalDocumentQueryJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DocumentRetryPortAdapterTest {

    @Mock
    private DigitalDocumentQueryJpaRepository jpaRepository;

    @InjectMocks
    private DocumentRetryPortAdapter adapter;

    @Test
    void resetForRetry_delegatesToJpaRepository() {
        var documentId = UUID.randomUUID();

        adapter.resetForRetry(documentId);

        verify(jpaRepository).resetForRetry(documentId);
    }
}
