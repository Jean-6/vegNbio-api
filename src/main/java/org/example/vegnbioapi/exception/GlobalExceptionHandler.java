package org.example.vegnbioapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.apache.tomcat.websocket.AuthenticationException;

import org.example.vegnbioapi.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }

    /**
     * missing parameters and invalid request
     */
    @ExceptionHandler(org.example.vegnbioapi.exception.BadRequestException.class)
    public ResponseEntity<ErrorResponse> handle400(BadRequestException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler({BindException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> handleBindingErrors(BadRequestException ex, HttpServletRequest request){
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI()), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handle401(BadCredentialsException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handle401(AuthenticationException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI()),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handle403(SecurityException ex, HttpServletRequest request){
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI()),HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handle404(NoHandlerFoundException ex,HttpServletRequest request){
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.NOT_FOUND,ex.getMessage(), request.getRequestURI()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle404Validation(MethodArgumentNotValidException ex, HttpServletRequest request){
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ":"+fieldError.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, message, request.getRequestURI()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handle409(ConflictException ex, HttpServletRequest request){
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI()),HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle500(Exception ex, HttpServletRequest request){
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI()),HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
