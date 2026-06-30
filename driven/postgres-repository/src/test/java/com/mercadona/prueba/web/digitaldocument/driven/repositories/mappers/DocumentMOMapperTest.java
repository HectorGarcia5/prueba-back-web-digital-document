package com.mercadona.prueba.web.digitaldocument.driven.repositories.mappers;

import com.mercadona.prueba.web.digitaldocument.driven.repositories.models.DigitalDocumentQueryMO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentMOMapperTest {

    private final DocumentMOMapper mapper = Mappers.getMapper(DocumentMOMapper.class);

    @Test
    void toView_mapsAllFieldsFromMo() {
        var mo = buildMo();

        var view = mapper.toView(mo);

        assertThat(view).isNotNull();
        assertThat(view.id()).isEqualTo(mo.getId());
        assertThat(view.employeeId()).isEqualTo(mo.getEmployeeId());
        assertThat(view.managedGroupId()).isEqualTo(mo.getManagedGroupId());
        assertThat(view.status()).isEqualTo(mo.getStatus());
        assertThat(view.failedStep()).isEqualTo(mo.getFailedStep());
        assertThat(view.storageKey()).isEqualTo(mo.getStorageKey());
        assertThat(view.checksum()).isEqualTo(mo.getChecksum());
        assertThat(view.createdAt()).isEqualTo(mo.getCreatedAt());
        assertThat(view.updatedAt()).isEqualTo(mo.getUpdatedAt());
        assertThat(view.publishedAt()).isEqualTo(mo.getPublishedAt());
    }

    @Test
    void toView_whenNullableDateFieldsAreNull_mapsToNull() {
        var mo = buildMoWithNullableDates();

        var view = mapper.toView(mo);

        assertThat(view.failedStep()).isNull();
        assertThat(view.storageKey()).isNull();
        assertThat(view.checksum()).isNull();
        assertThat(view.publishedAt()).isNull();
    }

    @Test
    void toView_whenMoIsNull_returnsNull() {
        var view = mapper.toView(null);

        assertThat(view).isNull();
    }

    // -----------------------------------------------------------------------
    // helpers — reflective construction because MO has @NoArgsConstructor only
    // -----------------------------------------------------------------------

    private static DigitalDocumentQueryMO buildMo() {
        try {
            var mo = new DigitalDocumentQueryMO();
            set(mo, "id", UUID.randomUUID());
            set(mo, "employeeId", "EMP1");
            set(mo, "managedGroupId", "MG1");
            set(mo, "status", "PUBLISHED");
            set(mo, "failedStep", "SOME_STEP");
            set(mo, "storageKey", "path/to/doc.pdf");
            set(mo, "checksum", "abc123");
            var now = OffsetDateTime.now();
            set(mo, "createdAt", now);
            set(mo, "updatedAt", now);
            set(mo, "publishedAt", now);
            return mo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static DigitalDocumentQueryMO buildMoWithNullableDates() {
        try {
            var mo = new DigitalDocumentQueryMO();
            set(mo, "id", UUID.randomUUID());
            set(mo, "employeeId", "EMP2");
            set(mo, "managedGroupId", "MG2");
            set(mo, "status", "PENDING");
            set(mo, "failedStep", null);
            set(mo, "storageKey", null);
            set(mo, "checksum", null);
            var now = OffsetDateTime.now();
            set(mo, "createdAt", now);
            set(mo, "updatedAt", now);
            set(mo, "publishedAt", null);
            return mo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void set(Object obj, String fieldName, Object value) throws Exception {
        var field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}
