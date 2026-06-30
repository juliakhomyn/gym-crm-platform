package com.gym.crm.core.exception;

import com.gia.openapi.model.ErrorResponse;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static com.gym.crm.core.exception.ApiError.AUTHENTICATION_ERROR;
import static com.gym.crm.core.exception.ApiError.AUTHORIZATION_ERROR;
import static com.gym.crm.core.exception.ApiError.DATABASE_ERROR;
import static com.gym.crm.core.exception.ApiError.NOT_FOUND_ERROR;
import static com.gym.crm.core.exception.ApiError.SERVICE_ERROR;
import static com.gym.crm.core.exception.ApiError.VALIDATION_ERROR;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {
    private static final String VALIDATION_ERROR_LOG_MESSAGE = "Validation error: {}";
    private static final String WORKLOAD_SERVICE = "workload-service";

    @ExceptionHandler(ValidationFailedException.class)
    public ResponseEntity<ErrorResponse> handleValidationFailedException(ValidationFailedException ex) {
        log.warn(VALIDATION_ERROR_LOG_MESSAGE, ex.getMessage());

        return buildErrorResponse(VALIDATION_ERROR, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn(VALIDATION_ERROR_LOG_MESSAGE, errorMessage);

        return buildErrorResponse(VALIDATION_ERROR, errorMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn(VALIDATION_ERROR_LOG_MESSAGE, errorMessage);

        return buildErrorResponse(VALIDATION_ERROR, errorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Malformed JSON request: {}", ex.getMessage());

        return buildErrorResponse(ApiError.VALIDATION_ERROR, "Malformed JSON: " + ex.getMostSpecificCause().getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Bad credentials: {}", ex.getMessage());

        return buildErrorResponse(AUTHENTICATION_ERROR, ex.getMessage());
    }

    @ExceptionHandler(UserAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleUserAuthenticationException(UserAuthenticationException ex) {
        log.warn("User authentication failed: {}", ex.getMessage());

        return buildErrorResponse(AUTHENTICATION_ERROR, ex.getMessage());
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLockedException(LockedException ex) {
        log.warn("User authentication failed: {}", ex.getMessage());

        return buildErrorResponse(AUTHENTICATION_ERROR, ex.getMessage());
    }

    @ExceptionHandler(UserAuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleUserAuthorizationException(UserAuthorizationException ex) {
        log.warn("User is not authorized for request operation: {}", ex.getMessage());

        return buildErrorResponse(AUTHORIZATION_ERROR, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("User is not authorized for request operation: {}", ex.getMessage());

        return buildErrorResponse(AUTHORIZATION_ERROR, ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("Requested data was not found: {}", ex.getMessage());

        return buildErrorResponse(NOT_FOUND_ERROR, ex.getMessage());
    }

    @ExceptionHandler(ServiceTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleServiceTimeoutException(ServiceTimeoutException ex) {
        String message = String.format("%s did not respond within 3s", WORKLOAD_SERVICE);
        log.warn("Timeout: {}", message, ex);

        return buildErrorResponse(ApiError.TIMEOUT_ERROR, message);
    }

    @ExceptionHandler(ServiceConnectionException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceConnectionException ex) {
        String message = String.format("Cannot connect to %s", WORKLOAD_SERVICE);
        log.warn("Connection error: {}", message, ex);

        return buildErrorResponse(ApiError.CONNECTION_ERROR, message);
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ErrorResponse> handlePersistenceException(PersistenceException ex) {
        log.error("Database access failure:", ex);

        return buildErrorResponse(DATABASE_ERROR, "");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unhandled exception:", ex);

        return buildErrorResponse(SERVICE_ERROR, "");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(ApiError apiError, String message) {
        message = StringUtils.isBlank(message) ? "" : message;

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(apiError.getCode());
        errorResponse.setErrorMessage(apiError.getMessage() + message);

        return new ResponseEntity<>(errorResponse, apiError.getStatus());
    }
}
