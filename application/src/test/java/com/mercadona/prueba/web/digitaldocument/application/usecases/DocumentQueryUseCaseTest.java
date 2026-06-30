package com.mercadona.prueba.web.digitaldocument.application.usecases;

import com.mercadona.prueba.web.digitaldocument.application.model.DigitalDocumentView;
import com.mercadona.prueba.web.digitaldocument.application.model.DocumentPage;
import com.mercadona.prueba.web.digitaldocument.application.ports.driven.DocumentQueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentQueryUseCaseTest {

    @Mock
    private DocumentQueryRepository repository;

    @InjectMocks
    private DocumentQueryUseCase useCase;

    @Test
    void findById_whenDocumentExists_returnsView() {
        var id = UUID.randomUUID();
        var view = aView(id);
        when(repository.findById(id)).thenReturn(Optional.of(view));

        var result = useCase.findById(id);

        assertThat(result).isPresent().contains(view);
        verify(repository).findById(id);
    }

    @Test
    void findById_whenDocumentNotFound_returnsEmpty() {
        var id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        var result = useCase.findById(id);

        assertThat(result).isEmpty();
        verify(repository).findById(id);
    }

    @Test
    void findByEmployee_whenDocumentExists_returnsView() {
        var view = aView(UUID.randomUUID());
        when(repository.findByEmployeeIdAndManagedGroupId("EMP1", "MG1")).thenReturn(Optional.of(view));

        var result = useCase.findByEmployee("EMP1", "MG1");

        assertThat(result).isPresent().contains(view);
        verify(repository).findByEmployeeIdAndManagedGroupId("EMP1", "MG1");
    }

    @Test
    void findByEmployee_whenDocumentNotFound_returnsEmpty() {
        when(repository.findByEmployeeIdAndManagedGroupId("EMP1", "MG1")).thenReturn(Optional.empty());

        var result = useCase.findByEmployee("EMP1", "MG1");

        assertThat(result).isEmpty();
    }

    @Test
    void findByStatus_delegatesToRepositoryAndReturnsPage() {
        var view = aView(UUID.randomUUID());
        var page = new DocumentPage(List.of(view), 1L, 0, 10);
        when(repository.findByStatus("PUBLISHED", 0, 10)).thenReturn(page);

        var result = useCase.findByStatus("PUBLISHED", 0, 10);

        assertThat(result).isEqualTo(page);
        assertThat(result.content()).containsExactly(view);
        assertThat(result.totalElements()).isEqualTo(1L);
        verify(repository).findByStatus("PUBLISHED", 0, 10);
    }

    @Test
    void findByStatus_whenNoResults_returnsEmptyPage() {
        var page = new DocumentPage(List.of(), 0L, 0, 10);
        when(repository.findByStatus("PENDING", 0, 10)).thenReturn(page);

        var result = useCase.findByStatus("PENDING", 0, 10);

        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isZero();
    }

    private static DigitalDocumentView aView(UUID id) {
        return new DigitalDocumentView(
                id, "EMP1", "MG1", "PUBLISHED", null, "key", "chk",
                OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now());
    }
}
