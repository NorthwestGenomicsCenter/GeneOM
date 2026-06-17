package org.uw.nwgc.multiomics.core.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;

@Log4j2
@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex, WebRequest request) {
        log.error(ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Unknown Error");
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handleIOException(Exception ex, WebRequest request) {
        log.error(ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Unauthorized");
    }
}
