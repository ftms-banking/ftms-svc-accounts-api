package ftms.svc.accounts.api.infrastructure.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BusinessRuleValidationException.class)
    public ResponseEntity<String> handleBusinessRuleValidationException(BusinessRuleValidationException ex){
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
}
