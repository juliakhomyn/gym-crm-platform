package com.gym.crm.workload.exception;

import com.gym.crm.workload.openapi.model.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void handleInvalidMessageException_shouldReturnErrorResponse() {
        InvalidMessageException exception = new InvalidMessageException("Date of birth must be in the past");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidMessageException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(ApiError.VALIDATION_ERROR.getCode());
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Invalid message error: Date of birth must be in the past");
    }

    @Test
    void handleMethodArgumentNotValidException_shouldReturnErrorResponse() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError firstNameError = new FieldError("traineeCreateRequest", "firstName", "must not be blank");
        FieldError lastNameError = new FieldError("traineeCreateRequest", "lastName", "must not be blank");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(firstNameError, lastNameError));

        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentNotValidException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(ApiError.VALIDATION_ERROR.getCode());
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Validation error: firstName must not be blank; lastName must not be blank");
    }

    @Test
    void handleMissingParameter_shouldReturnErrorResponse() {
        MissingServletRequestParameterException exception = new MissingServletRequestParameterException("year", "Integer");

        ResponseEntity<ErrorResponse> response = handler.handleMissingParameter(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(ApiError.VALIDATION_ERROR.getCode());
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Validation error: year is required");
    }

    @Test
    void handleConstraintViolationException_shouldReturnErrorResponse() {
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        Set<ConstraintViolation<?>> violations = Set.of(violation1, violation2);
        ConstraintViolationException exception = new ConstraintViolationException(violations);

        when(violation1.getMessage()).thenReturn("Username must not be blank");
        when(violation2.getMessage()).thenReturn("ID must be positive");

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolationException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(ApiError.VALIDATION_ERROR.getCode());
        assertThat(response.getBody().getErrorMessage()).contains("Validation error:")
                .contains("Username must not be blank")
                .contains("ID must be positive");
    }

    @Test
    void handleEntityNotFoundException_shouldReturnErrorResponse() {
        EntityNotFoundException exception = new EntityNotFoundException("User not found");

        ResponseEntity<ErrorResponse> response = handler.handleEntityNotFoundException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(ApiError.NOT_FOUND_ERROR.getCode());
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Requested data was not found: User not found");
    }

    @Test
    void handleGeneralException_shouldReturnErrorResponse() {
        Exception exception = new Exception();

        ResponseEntity<ErrorResponse> response = handler.handleGeneralException(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(ApiError.SERVICE_ERROR.getCode());
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Internal processing error");
    }
}
