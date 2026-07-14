package com.trademaster.ims.mobile.common.exception;

import com.trademaster.ims.mobile.common.response.MobileFieldError;
import java.util.List;

public class MobileValidationException extends RuntimeException {
    private final List<MobileFieldError> fieldErrors;

    public MobileValidationException(String field, String message) {
        this(List.of(new MobileFieldError(field, message)));
    }

    public MobileValidationException(List<MobileFieldError> fieldErrors) {
        super("One or more request values are invalid.");
        this.fieldErrors = List.copyOf(fieldErrors);
    }

    public List<MobileFieldError> getFieldErrors() { return fieldErrors; }
}
