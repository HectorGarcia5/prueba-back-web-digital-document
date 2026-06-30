package com.mercadona.prueba.web.digitaldocument.driven.repositories.adapters;

import com.mercadona.prueba.web.digitaldocument.driven.repositories.DigitalDocumentQueryJpaRepository;
import com.mercadona.prueba.web.digitaldocument.driven.repositories.mappers.DocumentMOMapper;
import com.mercadona.prueba.web.digitaldocument.driven.repositories.models.DigitalDocumentQueryMO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentQueryRepositoryAdapterTest {

    @Mock
    private DigitalDocumentQueryJpaRepository jpaRepository;

    @Mock
    private DocumentMOMapper mapper;

    @InjectMocks
    private DocumentQueryRepositoryAdapter adapter;

    // -----------------------------------------------------------------------
    // findById
    // -----------------------------------------------------------------------

    @Test
    void findById_whenMoExists_mapsAndReturnsView() {
        var id = UUID.randomUUID();
        var mo = stubMo(id, "EMP1", "MG1", "PUBLISHED");
        var view = aView(id);
        when(jpaRepository.findById(id)).thenReturn(Optional.of(mo));
        when(mapper.toView(mo)).thenReturn(view);

        var result = adapter.findById(id);

        assertThat(result).isPresent().contains(view);
        verify(jpaRepository).findById(id);
        verify(mapper).toView(mo);
    }

    @Test
    void findById_whenMoNotFound_returnsEmpty() {
        var id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        var result = adapter.findById(id);

        assertThat(result).isEmpty();
    }

    // -----------------------------------------------------------------------
    // findByEmployeeIdAndManagedGroupId
    // -----------------------------------------------------------------------

    @Test
    void findByEmployeeIdAndManagedGroupId_whenMoExists_mapsAndReturnsView() {
        var id = UUID.randomUUID();
        var mo = stubMo(id, "EMP1", "MG1", "ENRICHED");
        var view = aView(id);
        when(jpaRepository.findByEmployeeIdAndManagedGroupId("EMP1", "MG1")).thenReturn(Optional.of(mo));
        when(mapper.toView(mo)).thenReturn(view);

        var result = adapter.findByEmployeeIdAndManagedGroupId("EMP1", "MG1");

        assertThat(result).isPresent().contains(view);
        verify(jpaRepository).findByEmployeeIdAndManagedGroupId("EMP1", "MG1");
    }

    @Test
    void findByEmployeeIdAndManagedGroupId_whenNotFound_returnsEmpty() {
        when(jpaRepository.findByEmployeeIdAndManagedGroupId("EMP1", "MG1")).thenReturn(Optional.empty());

        var result = adapter.findByEmployeeIdAndManagedGroupId("EMP1", "MG1");

        assertThat(result).isEmpty();
    }

    // -----------------------------------------------------------------------
    // findByStatus
    // -----------------------------------------------------------------------

    @Test
    void findByStatus_buildsDocumentPageWithTotalElements() {
        var id1 = UUID.randomUUID();
        var id2 = UUID.randomUUID();
        var mo1 = stubMo(id1, "EMP1", "MG1", "PUBLISHED");
        var mo2 = stubMo(id2, "EMP2", "MG2", "PUBLISHED");
        var view1 = aView(id1);
        var view2 = aView(id2);
        var springPage = new PageImpl<>(List.of(mo1, mo2), PageRequest.of(0, 10), 2L);

        when(jpaRepository.findByStatus("PUBLISHED", PageRequest.of(0, 10))).thenReturn(springPage);
        when(mapper.toView(mo1)).thenReturn(view1);
        when(mapper.toView(mo2)).thenReturn(view2);

        var result = adapter.findByStatus("PUBLISHED", 0, 10);

        assertThat(result.content()).containsExactly(view1, view2);
        assertThat(result.totalElements()).isEqualTo(2L);
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        verify(jpaRepository).findByStatus("PUBLISHED", PageRequest.of(0, 10));
    }

    @Test
    void findByStatus_whenNothingFound_returnsEmptyPage() {
        var emptyPage = new PageImpl<DigitalDocumentQueryMO>(List.of(), PageRequest.of(1, 5), 0L);
        when(jpaRepository.findByStatus("PENDING", PageRequest.of(1, 5))).thenReturn(emptyPage);

        var result = adapter.findByStatus("PENDING", 1, 5);

        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isZero();
        assertThat(result.page()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(5);
    }

    @Test
    void findByStatus_passesCorrectPageRequestToJpa() {
        var emptyPage = new PageImpl<DigitalDocumentQueryMO>(List.of(), PageRequest.of(2, 15), 0L);
        when(jpaRepository.findByStatus(any(), any())).thenReturn(emptyPage);

        adapter.findByStatus("FAILED", 2, 15);

        verify(jpaRepository).findByStatus("FAILED", PageRequest.of(2, 15));
    }

    // -----------------------------------------------------------------------
    // helpers
    // -----------------------------------------------------------------------

    private static DigitalDocumentQueryMO stubMo(UUID id, String empId, String mgId, String status) {
        try {
            var mo = new DigitalDocumentQueryMO();
            set(mo, "id", id);
            set(mo, "employeeId", empId);
            set(mo, "managedGroupId", mgId);
            set(mo, "status", status);
            set(mo, "createdAt", OffsetDateTime.now());
            set(mo, "updatedAt", OffsetDateTime.now());
            return mo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static com.mercadona.prueba.web.digitaldocument.application.model.DigitalDocumentView aView(UUID id) {
        return new com.mercadona.prueba.web.digitaldocument.application.model.DigitalDocumentView(
                id, "EMP1", "MG1", "PUBLISHED", null, "key", "chk",
                OffsetDateTime.now(), OffsetDateTime.now(), null);
    }

    private static void set(Object obj, String fieldName, Object value) throws Exception {
        var field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}
