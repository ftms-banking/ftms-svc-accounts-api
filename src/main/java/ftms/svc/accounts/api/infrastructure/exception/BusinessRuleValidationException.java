package ftms.svc.accounts.api.infrastructure.exception;

public class BusinessRuleValidationException extends RuntimeException{

    private String errorCode;

    public BusinessRuleValidationException(String message){
        super(message);
    }

    public BusinessRuleValidationException(String message, String errorCode){
        super(message);
        this.errorCode = errorCode;
    }


}
