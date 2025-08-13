package com.example.bankcards.exception;

import com.example.bankcards.constants.ExceptionConstants;
import com.example.bankcards.constants.LogConstants;
import com.example.bankcards.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException e) {
        log.warn(LogConstants.NOT_FOUND, e.getMessage());
        return buildErrorResponse(e.getMessage());
    }

    @ExceptionHandler(CardAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCardAlreadyExists(final CardAlreadyExistsException e) {
        log.warn(LogConstants.CARD_ALREADY_EXISTS, e.getMessage());
        return buildErrorResponse(e.getMessage());
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleUnauthorizedOperation(final UnauthorizedOperationException e) {
        log.warn(LogConstants.UNAUTHORIZED_OPERATION, e.getMessage());
        return buildErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final ValidationException e) {
        log.warn(LogConstants.VALIDATION_FAILED, e.getMessage());
        return buildErrorResponse(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(final IllegalArgumentException e) {
        log.warn(LogConstants.BAD_REQUEST, e.getMessage());
        return buildErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(final ConstraintViolationException e) {
        log.warn(LogConstants.CONSTRAINT_VIOLATION, e.getMessage());
        String msg = ExceptionConstants.VALIDATION_ERROR_PREFIX + " " + e.getMessage();
        return buildErrorResponse(msg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(final Exception e) {
        log.error(LogConstants.UNEXPECTED_ERROR, e);
        return buildErrorResponse(ExceptionConstants.INTERNAL_ERROR);
    }

    private ErrorResponse buildErrorResponse(String message) {
        return ErrorResponse.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
