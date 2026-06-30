package com.mercadona.prueba.web.digitaldocument.driving.controllers.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadona.prueba.web.digitaldocument.application.usecases.RepublishDocumentUseCase;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.error.DigitalDocumentControllerAdvice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentRepublishController.class)
@Import(DigitalDocumentControllerAdvice.class)
class DocumentRepublishControllerTest {

    private static final String URL = "/api/v1/utils/documents/republish";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RepublishDocumentUseCase republishUseCase;

    // -----------------------------------------------------------------------
    // happy path
    // -----------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "DIGITAL_DOCUMENT_REPUBLISH")
    void republish_withValidBody_returns202AndDelegates() throws Exception {
        var docId = UUID.randomUUID();
        var body = Map.of(
                "documentId", docId.toString(),
                "employeeId", "EMP42",
                "managedGroupId", "MG99");

        mockMvc.perform(post(URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isAccepted());

        verify(republishUseCase).republish(eq(docId), eq("EMP42"), eq("MG99"));
    }

    // -----------------------------------------------------------------------
    // validation: missing / blank fields
    // -----------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "DIGITAL_DOCUMENT_REPUBLISH")
    void republish_whenDocumentIdMissing_returns400() throws Exception {
        var body = Map.of(
                "employeeId", "EMP42",
                "managedGroupId", "MG99");

        mockMvc.perform(post(URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(republishUseCase);
    }

    @Test
    @WithMockUser(roles = "DIGITAL_DOCUMENT_REPUBLISH")
    void republish_whenEmployeeIdBlank_returns400() throws Exception {
        var body = Map.of(
                "documentId", UUID.randomUUID().toString(),
                "employeeId", "",
                "managedGroupId", "MG99");

        mockMvc.perform(post(URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(republishUseCase);
    }

    @Test
    @WithMockUser(roles = "DIGITAL_DOCUMENT_REPUBLISH")
    void republish_whenManagedGroupIdBlank_returns400() throws Exception {
        var body = Map.of(
                "documentId", UUID.randomUUID().toString(),
                "employeeId", "EMP1",
                "managedGroupId", "");

        mockMvc.perform(post(URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(republishUseCase);
    }

}
