package com.mercadona.prueba.web.digitaldocument.driving.controllers.error;

import com.mercadona.framework.cna.commons.domain.MercadonaBusinessException;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.dto.ErrorResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DigitalDocumentControllerAdviceTest {

    @InjectMocks
    private DigitalDocumentControllerAdvice advice;

    @Mock
    private BindingResult bindingResult;

    // -----------------------------------------------------------------------
    // MissingServletRequestParameterException
    // -----------------------------------------------------------------------

    @Test
    void handleMissingParam_returns400WithParameterName() {
        var ex = new MissingServletRequestParameterException("status", "String");

        var response = advice.handleMissingParam(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError().getCode()).isEqualTo("MISSING_PARAMETER");
        assertThat(response.getBody().getError().getDescription()).contains("status");
    }

    // -----------------------------------------------------------------------
    // MethodArgumentTypeMismatchException
    // -----------------------------------------------------------------------

    @Test
    void handleTypeMismatch_returns400WithParameterName() throws Exception {
        // Build a MethodParameter from a known method to satisfy Spring's constructor
        var method = getClass().getDeclaredMethod("handleTypeMismatch_returns400WithParameterName");
        var methodParameter = new MethodParameter(method, -1);
        var ex = new MethodArgumentTypeMismatchException(
                "bad-uuid", UUID.class, "documentId", methodParameter, null);

        var response = advice.handleTypeMismatch(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError().getCode()).isEqualTo("INVALID_PARAMETER");
        assertThat(response.getBody().getError().getDescription()).contains("documentId");
    }

    // -----------------------------------------------------------------------
    // MethodArgumentNotValidException
    // -----------------------------------------------------------------------

    @Test
    void handleValidation_returns400WithFieldErrors() throws Exception {
        var fieldError = new FieldError("republishRequest", "employeeId", "must not be blank");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        var ex = new MethodArgumentNotValidException(null, bindingResult);

        var response = advice.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponseDto body = response.getBody();
        assertThat(body.getError().getCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(body.getError().getDescription()).contains("employeeId").contains("must not be blank");
    }

    @Test
    void handleValidation_multipleFieldErrors_joinsAllInDescription() throws Exception {
        var error1 = new FieldError("req", "employeeId", "must not be blank");
        var error2 = new FieldError("req", "managedGroupId", "must not be blank");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));
        var ex = new MethodArgumentNotValidException(null, bindingResult);

        var response = advice.handleValidation(ex);

        assertThat(response.getBody().getError().getDescription())
                .contains("employeeId")
                .contains("managedGroupId");
    }

    // -----------------------------------------------------------------------
    // MercadonaBusinessException
    // -----------------------------------------------------------------------

    @Test
    void handleBusinessException_returns422WithErrorCodeAndMessage() {
        var ex = new MercadonaBusinessException("Document not found", "DOC_NOT_FOUND");

        var response = advice.handleBusinessException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody().getError().getCode()).isEqualTo("DOC_NOT_FOUND");
        assertThat(response.getBody().getError().getDescription()).isEqualTo("Document not found");
    }

    @Test
    void handleBusinessException_whenErrorCodeIsNull_setsNullCode() {
        var ex = new MercadonaBusinessException("some business error");

        var response = advice.handleBusinessException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody().getError().getDescription()).isEqualTo("some business error");
    }

    // -----------------------------------------------------------------------
    // IllegalArgumentException
    // -----------------------------------------------------------------------

    @Test
    void handleIllegalArgument_returns400WithMessage() {
        var ex = new IllegalArgumentException("bad request value");

        var response = advice.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError().getCode()).isEqualTo("BAD_REQUEST");
        assertThat(response.getBody().getError().getDescription()).isEqualTo("bad request value");
    }

    // -----------------------------------------------------------------------
    // Generic Exception
    // -----------------------------------------------------------------------

    @Test
    void handleUnexpected_returns500WithGenericMessage() {
        var ex = new RuntimeException("unexpected failure");

        var response = advice.handleUnexpected(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getError().getCode()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().getError().getDescription()).isEqualTo("Internal server error");
    }
}
