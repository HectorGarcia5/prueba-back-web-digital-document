package com.mercadona.prueba.web.digitaldocument.application.usecases;

import com.mercadona.framework.cna.commons.domain.MercadonaBusinessException;
import com.mercadona.prueba.web.digitaldocument.application.exception.DocumentNotFoundException;
import com.mercadona.prueba.web.digitaldocument.application.model.DigitalDocumentView;
import com.mercadona.prueba.web.digitaldocument.application.model.DocumentContentUrl;
import com.mercadona.prueba.web.digitaldocument.application.ports.driven.DocumentQueryRepository;
import com.mercadona.prueba.web.digitaldocument.application.ports.driven.DocumentStoragePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDocumentContentUseCaseTest {

    @Mock private DocumentQueryRepository queryRepository;
    @Mock private DocumentStoragePort storagePort;
    @InjectMocks private GetDocumentContentUseCase useCase;

    @Test
    void getContentUrl_whenDocumentStoredAndKeyPresent_returnsSignedUrl() throws Exception {
        var id = UUID.randomUUID();
        var storageKey = "employee-documents/" + id + ".pdf";
        var expectedUri = URI.create("http://minio/bucket/" + id + ".pdf");
        var expectedUrl = new DocumentContentUrl(expectedUri, 120L);
        when(queryRepository.findById(id)).thenReturn(Optional.of(aView(id, "PUBLISHED", storageKey)));
        when(storagePort.getSignedUrl(storageKey)).thenReturn(expectedUrl);

        var result = useCase.getContentUrl(id);

        assertThat(result.signedUrl()).isEqualTo(expectedUri);
        assertThat(result.expiresInSeconds()).isEqualTo(120L);
        verify(storagePort).getSignedUrl(storageKey);
    }

    @Test
    void getContentUrl_whenDocumentNotFound_throwsDocumentNotFoundException() {
        var id = UUID.randomUUID();
        when(queryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.getContentUrl(id))
                .isInstanceOf(DocumentNotFoundException.class);
        verifyNoInteractions(storagePort);
    }

    @Test
    void getContentUrl_whenStorageKeyIsNull_throwsMercadonaBusinessException() {
        var id = UUID.randomUUID();
        when(queryRepository.findById(id)).thenReturn(Optional.of(aView(id, "PENDING", null)));

        assertThatThrownBy(() -> useCase.getContentUrl(id))
                .isInstanceOf(MercadonaBusinessException.class)
                .hasMessageContaining("PENDING")
                .satisfies(e -> assertThat(((MercadonaBusinessException) e).getErrorCode()).isEqualTo("DOCUMENT_NOT_STORED"));
        verifyNoInteractions(storagePort);
    }

    @Test
    void getContentUrl_whenStorageKeyIsNull_exceptionContainsErrorCode() {
        var id = UUID.randomUUID();
        when(queryRepository.findById(id)).thenReturn(Optional.of(aView(id, "FAILED", null)));

        assertThatThrownBy(() -> useCase.getContentUrl(id))
                .isInstanceOf(MercadonaBusinessException.class)
                .satisfies(e -> assertThat(((MercadonaBusinessException) e).getErrorCode()).isEqualTo("DOCUMENT_NOT_STORED"));
    }

    private static DigitalDocumentView aView(UUID id, String status, String storageKey) {
        return new DigitalDocumentView(id, "EMP1", "MG1", status, null, storageKey,
                "chk", OffsetDateTime.now(), OffsetDateTime.now(), null);
    }
}
