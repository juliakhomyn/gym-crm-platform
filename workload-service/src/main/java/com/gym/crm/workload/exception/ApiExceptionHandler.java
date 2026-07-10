package com.gym.crm.workload.exception;

import com.gym.crm.workload.openapi.model.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static com.gym.crm.workload.exception.ApiError.DATABASE_ERROR;
import static com.gym.crm.workload.exception.ApiError.INVALID_MESSAGE_ERROR;
import static com.gym.crm.workload.exception.ApiError.NOT_FOUND_ERROR;
import static com.gym.crm.workload.exception.ApiError.SERVICE_ERROR;
import static com.gym.crm.workload.exception.ApiError.VALIDATION_ERROR;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final String VALIDATION_ERROR_LOG_MESSAGE = "Validation error: {}";

    @ExceptionHandler(InvalidMessageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMessageException(InvalidMessageException ex) {
        log.warn("{}: {}", INVALID_MESSAGE_ERROR.getMessage(), ex.getMessage());

        return buildErrorResponse(INVALID_MESSAGE_ERROR, ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
        return validationResponse(ex.getParameterName() + " is required");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> "%s %s".formatted(
                        error.getField(),
                        error.getDefaultMessage()))
                .collect(Collectors.joining("; "));

        return validationResponse(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        return validationResponse(
                ex.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining("; "))
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {

        log.warn("Requested data was not found: {}", ex.getMessage());

        return buildErrorResponse(NOT_FOUND_ERROR, ex.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException ex) {
        log.error("Database access failure:", ex);

        return buildErrorResponse(DATABASE_ERROR, "");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unhandled exception:", ex);

        return buildErrorResponse(SERVICE_ERROR, "");
    }

    private ResponseEntity<ErrorResponse> validationResponse(String message) {
        log.warn(VALIDATION_ERROR_LOG_MESSAGE, message);

        return buildErrorResponse(VALIDATION_ERROR, message);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(ApiError apiError, String message) {
        ErrorResponse response = new ErrorResponse();
        response.setErrorCode(apiError.getCode());
        response.setErrorMessage(apiError.getMessage() + StringUtils.defaultString(message));

        return ResponseEntity.status(apiError.getStatus()).body(response);
    }
}
