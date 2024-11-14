/* (C)2024 */
package se.seb.embedded.coding_assignment.app.v1.error;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import se.seb.embedded.coding_assignment.app.v1.error.model.ApiError;
import se.seb.embedded.coding_assignment.app.v1.error.model.BadRequestApiError;
import se.seb.embedded.coding_assignment.app.v1.error.model.ErrorDetail;

/** Global error handler for the API. On place to handle all or specific error thrown by the application. */
@Log
@RestControllerAdvice
public class ApiErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public BadRequestApiError validationExceptionHandling(
            HttpServletRequest request, MethodArgumentNotValidException e) {
        BadRequestApiError error = new BadRequestApiError();
        error.setTitle(HttpStatus.BAD_REQUEST.name());
        error.setPath(request.getRequestURI());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setTimestamp(LocalDateTime.now());

        List<ErrorDetail> errorsList = e.getBindingResult().getFieldErrors().stream()
                .map(err -> new ErrorDetail(err.getCode(), err.getField(), err.getDefaultMessage()))
                .toList();
        error.setErrors(errorsList);
        return error;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError otherExceptionHandling(HttpServletRequest request, Exception e) {
        ApiError error = new ApiError();
        error.setTitle(HttpStatus.INTERNAL_SERVER_ERROR.name());
        error.setPath(request.getRequestURI());
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setTimestamp(LocalDateTime.now());

        String refId = RandomStringUtils.random(26, true, true);
        error.setRefid(refId);

        log.log(Level.SEVERE, e, () -> "Something went wrong. RefId : " + refId);

        return error;
    }
}
