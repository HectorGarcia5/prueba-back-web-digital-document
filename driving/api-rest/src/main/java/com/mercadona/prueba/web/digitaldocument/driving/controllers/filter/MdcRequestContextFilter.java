package com.mercadona.prueba.web.digitaldocument.driving.controllers.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter that populates the MDC context with request-scoped identifiers
 * and guarantees cleanup after the response is written, regardless of outcome.
 *
 * <p>Extracts {@code X-Correlation-Id} and {@code X-Request-Id} headers when present.
 * Endpoint-specific keys (employeeId, documentId, etc.) are set here from path variables
 * extracted via simple URI matching to keep controllers free of MDC concerns.
 */
@Component
@Order(1)
public class MdcRequestContextFilter extends OncePerRequestFilter {

  private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
  private static final String REQUEST_ID_HEADER = "X-Request-Id";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      populateMdc(request);
      filterChain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }

  private void populateMdc(HttpServletRequest request) {
    String correlationId = request.getHeader(CORRELATION_ID_HEADER);
    if (correlationId != null) {
      MDC.put("correlationId", correlationId);
    }

    String requestId = request.getHeader(REQUEST_ID_HEADER);
    if (requestId != null) {
      MDC.put("requestId", requestId);
    }

    extractPathVariables(request.getRequestURI());
  }

  private void extractPathVariables(String uri) {
    if (uri == null) {
      return;
    }

    // /api/v1/employees/{employeeId}/managed-groups/{managedGroupId}/document
    if (uri.contains("/employees/") && uri.contains("/managed-groups/")) {
      String[] parts = uri.split("/");
      for (int i = 0; i < parts.length - 1; i++) {
        if ("employees".equals(parts[i])) {
          MDC.put("employeeId", parts[i + 1]);
        }
        if ("managed-groups".equals(parts[i])) {
          MDC.put("managedGroupId", parts[i + 1]);
        }
      }
      return;
    }

    // /api/v1/documents/{documentId} or /api/v1/documents/{documentId}/status
    if (uri.contains("/documents/")) {
      String[] parts = uri.split("/");
      for (int i = 0; i < parts.length - 1; i++) {
        if ("documents".equals(parts[i])) {
          MDC.put("documentId", parts[i + 1]);
          break;
        }
      }
    }
  }
}
