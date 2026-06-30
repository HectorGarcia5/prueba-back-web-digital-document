package com.mercadona.prueba.web.digitaldocument.application.usecases;

import com.mercadona.prueba.web.digitaldocument.application.exception.DocumentNotFoundException;
import com.mercadona.prueba.web.digitaldocument.application.exception.DocumentNotRetryableException;
import com.mercadona.prueba.web.digitaldocument.application.model.DigitalDocumentView;
import com.mercadona.prueba.web.digitaldocument.application.ports.driven.DocumentQueryRepository;
import com.mercadona.prueba.web.digitaldocument.application.ports.driven.DocumentRetryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetryDocumentUseCaseTest {

    @Mock private DocumentQueryRepository queryRepository;
    @Mock private DocumentRetryPort retryPort;
    @InjectMocks private RetryDocumentUseCase useCase;

    @Test
    void retry_whenDocumentFailed_delegatesToRetryPort() {
        var id = UUID.randomUUID();
        when(queryRepository.findById(id)).thenReturn(Optional.of(aView(id, "FAILED")));

        useCase.retry(id);

        verify(retryPort).resetForRetry(id);
    }

    @Test
    void retry_whenDocumentNotFound_throwsDocumentNotFoundException() {
        var id = UUID.randomUUID();
        when(queryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.retry(id))
                .isInstanceOf(DocumentNotFoundException.class);
        verifyNoInteractions(retryPort);
    }

    @Test
    void retry_whenDocumentNotFailed_throwsDocumentNotRetryableException() {
        var id = UUID.randomUUID();
        when(queryRepository.findById(id)).thenReturn(Optional.of(aView(id, "PUBLISHED")));

        assertThatThrownBy(() -> useCase.retry(id))
                .isInstanceOf(DocumentNotRetryableException.class);
        verifyNoInteractions(retryPort);
    }

    @Test
    void retry_whenDocumentPending_throwsDocumentNotRetryableException() {
        var id = UUID.randomUUID();
        when(queryRepository.findById(id)).thenReturn(Optional.of(aView(id, "PENDING")));

        assertThatThrownBy(() -> useCase.retry(id))
                .isInstanceOf(DocumentNotRetryableException.class);
        verifyNoInteractions(retryPort);
    }

    private static DigitalDocumentView aView(UUID id, String status) {
        return new DigitalDocumentView(id, "EMP1", "MG1", status, null, null,
                null, OffsetDateTime.now(), OffsetDateTime.now(), null);
    }
}
