package ir.darkdeveloper.sma.exceptions.advice;

import ir.darkdeveloper.sma.dto.ExceptionDto;
import ir.darkdeveloper.sma.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({PasswordException.class, BadRequestException.class})
    public ResponseEntity<?> handleBadRequests(RuntimeException e) {
        return createResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbiddenRequests(ForbiddenException e) {
        return createResponseEntity(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InternalException.class)
    public ResponseEntity<?> handleInternalErrors(InternalException e) {
        return createResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<?> handleNoContentRequestsAndResponses(NoContentException e) {
        return createResponseEntity(e, HttpStatus.NO_CONTENT);
    }


    private ResponseEntity<?> createResponseEntity(RuntimeException e, HttpStatus status) {
        var ed = new ExceptionDto(e.getLocalizedMessage(), status, LocalDateTime.now());
        return new ResponseEntity<>(ed, status);
    }

}
