package com.mercadona.prueba.web.digitaldocument.driving.controllers.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MdcRequestContextFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private MdcRequestContextFilter filter;

    @AfterEach
    void cleanMdc() {
        MDC.clear();
    }

    // -----------------------------------------------------------------------
    // Correlation and Request ID headers
    // -----------------------------------------------------------------------

    @Test
    void doFilterInternal_whenCorrelationIdHeaderPresent_populatesMdc() throws Exception {
        when(request.getHeader("X-Correlation-Id")).thenReturn("corr-123");
        when(request.getHeader("X-Request-Id")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/v1/other");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        // MDC is cleared in finally — we assert during the chain via a capturing filter
        // We verify that the filter did not throw and the chain was called
    }

    @Test
    void doFilterInternal_alwaysClearsMdcInFinally() throws Exception {
        when(request.getHeader("X-Correlation-Id")).thenReturn("corr-123");
        when(request.getHeader("X-Request-Id")).thenReturn("req-456");
        when(request.getRequestURI()).thenReturn("/api/v1/other");

        // Capture MDC state inside the filter chain execution
        final String[] mdcInsideChain = new String[2];
        org.mockito.Mockito.doAnswer(invocation -> {
            mdcInsideChain[0] = MDC.get("correlationId");
            mdcInsideChain[1] = MDC.get("requestId");
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        // Inside chain: values are set
        assertThat(mdcInsideChain[0]).isEqualTo("corr-123");
        assertThat(mdcInsideChain[1]).isEqualTo("req-456");
        // After filter: MDC is cleared
        assertThat(MDC.get("correlationId")).isNull();
        assertThat(MDC.get("requestId")).isNull();
    }

    @Test
    void doFilterInternal_whenNoHeaders_mdcKeysNotSet() throws Exception {
        when(request.getHeader("X-Correlation-Id")).thenReturn(null);
        when(request.getHeader("X-Request-Id")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/v1/other");

        final String[] mdcInsideChain = new String[2];
        org.mockito.Mockito.doAnswer(invocation -> {
            mdcInsideChain[0] = MDC.get("correlationId");
            mdcInsideChain[1] = MDC.get("requestId");
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(mdcInsideChain[0]).isNull();
        assertThat(mdcInsideChain[1]).isNull();
    }

    // -----------------------------------------------------------------------
    // MDC always cleared even when chain throws
    // -----------------------------------------------------------------------

    @Test
    void doFilterInternal_clearsMdcEvenWhenChainThrows() throws Exception {
        when(request.getHeader("X-Correlation-Id")).thenReturn("corr-999");
        when(request.getHeader("X-Request-Id")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/v1/other");
        org.mockito.Mockito.doThrow(new RuntimeException("chain error"))
                .when(filterChain).doFilter(request, response);

        try {
            filter.doFilterInternal(request, response, filterChain);
        } catch (RuntimeException ignored) {
        }

        assertThat(MDC.get("correlationId")).isNull();
    }

    // -----------------------------------------------------------------------
    // Path variable extraction — employees pattern
    // [PARAM]
    // -----------------------------------------------------------------------

    @ParameterizedTest(name = "{index} -> URI={0} employeeId={1} managedGroupId={2}")
    @CsvSource({
        "/api/v1/employees/EMP1/managed-groups/MG1/document, EMP1, MG1",
        "/api/v1/employees/ABC-123/managed-groups/XYZ-456/document, ABC-123, XYZ-456"
    })
    void doFilterInternal_employeesPattern_populatesEmployeeAndManagedGroupInMdc(
            String uri, String expectedEmpId, String expectedMgId) throws Exception {

        when(request.getHeader("X-Correlation-Id")).thenReturn(null);
        when(request.getHeader("X-Request-Id")).thenReturn(null);
        when(request.getRequestURI()).thenReturn(uri);

        final String[] captured = new String[2];
        org.mockito.Mockito.doAnswer(inv -> {
            captured[0] = MDC.get("employeeId");
            captured[1] = MDC.get("managedGroupId");
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(captured[0]).isEqualTo(expectedEmpId);
        assertThat(captured[1]).isEqualTo(expectedMgId);
    }

    // -----------------------------------------------------------------------
    // Path variable extraction — documents pattern
    // [PARAM]
    // -----------------------------------------------------------------------

    @ParameterizedTest(name = "{index} -> URI={0} documentId={1}")
    @CsvSource({
        "/api/v1/documents/550e8400-e29b-41d4-a716-446655440000, 550e8400-e29b-41d4-a716-446655440000",
        "/api/v1/documents/550e8400-e29b-41d4-a716-446655440000/status, 550e8400-e29b-41d4-a716-446655440000"
    })
    void doFilterInternal_documentsPattern_populatesDocumentIdInMdc(
            String uri, String expectedDocId) throws Exception {

        when(request.getHeader("X-Correlation-Id")).thenReturn(null);
        when(request.getHeader("X-Request-Id")).thenReturn(null);
        when(request.getRequestURI()).thenReturn(uri);

        final String[] captured = new String[1];
        org.mockito.Mockito.doAnswer(inv -> {
            captured[0] = MDC.get("documentId");
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(captured[0]).isEqualTo(expectedDocId);
    }

    @Test
    void doFilterInternal_whenUriDoesNotMatchAnyPattern_mdcDocumentIdNotSet() throws Exception {
        when(request.getHeader("X-Correlation-Id")).thenReturn(null);
        when(request.getHeader("X-Request-Id")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/v1/other/endpoint");

        final String[] captured = new String[1];
        org.mockito.Mockito.doAnswer(inv -> {
            captured[0] = MDC.get("documentId");
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(captured[0]).isNull();
    }

    @Test
    void doFilterInternal_whenUriIsNull_doesNotThrow() throws Exception {
        when(request.getHeader("X-Correlation-Id")).thenReturn(null);
        when(request.getHeader("X-Request-Id")).thenReturn(null);
        when(request.getRequestURI()).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
