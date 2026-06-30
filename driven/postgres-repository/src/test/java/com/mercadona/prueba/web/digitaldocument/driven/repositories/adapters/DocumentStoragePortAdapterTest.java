package com.mercadona.prueba.web.digitaldocument.driven.repositories.adapters;

import com.mercadona.framework.cna.lib.bucket.service.BucketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentStoragePortAdapterTest {

    private static final String BUCKET_ID = "digital-documents";

    @Mock
    private BucketService bucketService;

    private DocumentStoragePortAdapter buildAdapter() {
        return new DocumentStoragePortAdapter(bucketService, BUCKET_ID);
    }

    @Test
    void getSignedUrl_returnsUriAndExpiration() throws Exception {
        var storageKey = "employee-documents/abc.pdf";
        var expectedUri = URI.create("http://minio/employee-documents/abc.pdf?sig=xyz");
        when(bucketService.getSignedUrl(BUCKET_ID, storageKey, 120L)).thenReturn(expectedUri);

        var result = buildAdapter().getSignedUrl(storageKey);

        assertThat(result.signedUrl()).isEqualTo(expectedUri);
        assertThat(result.expiresInSeconds()).isEqualTo(120L);
    }

    @Test
    void getSignedUrl_whenBucketServiceThrows_wrapsInRuntimeException() throws Exception {
        var storageKey = "employee-documents/missing.pdf";
        when(bucketService.getSignedUrl(BUCKET_ID, storageKey, 120L))
                .thenThrow(new RuntimeException("bucket unavailable"));

        assertThatThrownBy(() -> buildAdapter().getSignedUrl(storageKey))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("bucket unavailable");
    }
}
