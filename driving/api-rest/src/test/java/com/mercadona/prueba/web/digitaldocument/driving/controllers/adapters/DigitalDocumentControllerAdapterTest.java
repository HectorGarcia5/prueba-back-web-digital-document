package com.mercadona.prueba.web.digitaldocument.driving.controllers.adapters;

import com.mercadona.prueba.web.digitaldocument.application.exception.DocumentNotFoundException;
import com.mercadona.prueba.web.digitaldocument.application.model.DigitalDocumentView;
import com.mercadona.prueba.web.digitaldocument.application.model.DocumentContentUrl;
import com.mercadona.prueba.web.digitaldocument.application.model.DocumentPage;
import com.mercadona.prueba.web.digitaldocument.application.usecases.DocumentQueryUseCase;
import com.mercadona.prueba.web.digitaldocument.application.usecases.GetDocumentContentUseCase;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.error.DigitalDocumentControllerAdvice;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.mappers.DocumentDTOMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DigitalDocumentControllerAdapter.class)
@Import({DocumentDTOMapperImpl.class, DigitalDocumentControllerAdvice.class})
class DigitalDocumentControllerAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentQueryUseCase queryUseCase;

    @MockBean
    private GetDocumentContentUseCase getDocumentContentUseCase;

    @Test
    @WithMockUser(roles = "DIGITAL_DOCUMENT_READ")
    void getDocumentByEmployee_whenFound_returns200WithBody() throws Exception {
        var id = UUID.randomUUID();
        var view = aView(id);
        when(queryUseCase.findByEmployee("EMP1", "MG1")).thenReturn(Optional.of(view));

        mockMvc.perform(get("/api/v1/employees/EMP1/managed-groups/MG1/document"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.employeeId").value("EMP1"))
                .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }

    @Test
    @WithMockUser(roles = "DIGITAL_DOCUMENT_READ")
    void getDocumentByEmployee_whenNotFound_returns404() throws Exception {
        when(queryUseCase.findByEmployee("EMP1", "MG1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/employees/EMP1/managed-groups/MG1/document"))
                .andExpect(status().isNotFound());
    }

    // NOTE: Without the FWK SecurityConfiguration active (disabled via test application.yml),
    // @EnableMethodSecurity is also inactive and @PreAuthorize is not evaluated.
    // Role-based access is covered by integration tests where the full security stack is loaded.
    @Test
    @WithMockUser(roles = "DIGITAL_DOCUMENT_READ")
    void getDocumentByEmployee_withExistingDoc_returns200WithAllFields() throws Exception {
        var id = UUID.randomUUID();
        var view = aView(id);
        when(queryUseCase.findByEmployee("EMP1", "MG1")).thenReturn(Optional.of(view));

        mockMvc.perform(get("/api/v1/employees/EMP1/managed-groups/MG1/document"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.managedGroupId").value("MG1"))
                .andExpect(jsonPath("$.checksum").value("chk123"));
    }

    @Test
    @WithMockUser(roles = "DIGITAL_DOCUMENT_READ")
    void getDocumentById_whenFound_returns200WithBody() throws Exception {
        var id = UUID.randomUUID();
        var view = aView(id);
        when(queryUseCase.findById(id)).thenReturn(Optional.of(view));

        mockMvc.perform(get("/api/v1/documents/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    @WithMockUser(roles = "DIGITAL_DOCUMENT_READ")
    void getDocumentById_whenNotFound_returns404() throws Exception {
        var id = UUID.randomUUID();
        when(queryUseCase.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/documents/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "DIGITAL_DOCUMENT_READ")
    void getDocumentStatus_whenFound_returns200WithStatusField() throws Exception {
        var id = UUID.randomUUID();
        var view = aView(id);
        when(queryUseCase.findById(id)).thenReturn(Optional.of(view));

        mockMvc.perform(get("/api/v1/documents/{id}/status", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentId").value(id.toString()))
                .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }

    @Test
    @WithMockUser(roles = "DIGITAL_DOCUMENT_READ")
    void getDocumentStatus_whenNotFound_returns404() throws Exception {
        var id = UUID.randomUUID();
        when(queryUseCase.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/documents/{id}/status", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "DIGITAL_DOCUMENT_READ")
    void listDocumentsByStatus_withExplicitPageAndSize_returns200WithPage() throws Exception {
        var view = aView(UUID.randomUUID());
        var page = new DocumentPage(List.of(view), 1L, 0, 10);
        when(queryUseCase.findByStatus("PUBLISHED", 0, 10)).thenReturn(page);

        mockMvc.perform(get("/api/v1/documents")
                        .param("status", "PUBLISHED")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].status").value("PUBLISHED"));
    }

    @Test
    @WithMockUser(roles = "DIGITAL_DOCUMENT_READ")
    void listDocumentsByStatus_withDefaultPageAndSize_usesDefaults() throws Exception {
        var page = new DocumentPage(List.of(), 0L, 0, 20);
        when(queryUseCase.findByStatus("PENDING", 0, 20)).thenReturn(page);

        mockMvc.perform(get("/api/v1/documents").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }


    @Test
    @WithMockUser(roles = "DIGITAL_DOCUMENT_READ")
    void getDocumentContent_whenStoredDocument_returns302WithLocation() throws Exception {
        var id = UUID.randomUUID();
        var signedUri = URI.create("http://minio/employee-documents/" + id + ".pdf?sig=abc");
        when(getDocumentContentUseCase.getContentUrl(id))
                .thenReturn(new DocumentContentUrl(signedUri, 120L));

        mockMvc.perform(get("/api/v1/documents/{id}/content", id))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", signedUri.toString()));
    }

    @Test
    @WithMockUser(roles = "DIGITAL_DOCUMENT_READ")
    void getDocumentContent_whenDocumentNotFound_returns404() throws Exception {
        var id = UUID.randomUUID();
        when(getDocumentContentUseCase.getContentUrl(id))
                .thenThrow(new DocumentNotFoundException(id));

        mockMvc.perform(get("/api/v1/documents/{id}/content", id))
                .andExpect(status().isNotFound());
    }

    private static DigitalDocumentView aView(UUID id) {
        return new DigitalDocumentView(
                id, "EMP1", "MG1", "PUBLISHED", null, "key/doc.pdf", "chk123",
                OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now());
    }
}

// NOTE: appended after original file — tests for getDocumentContent (302 redirect)
