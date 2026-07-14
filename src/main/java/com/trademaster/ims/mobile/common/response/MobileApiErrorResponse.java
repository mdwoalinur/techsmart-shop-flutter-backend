package com.trademaster.ims.mobile.common.response;

import java.time.Instant;
import java.util.List;

public record MobileApiErrorResponse(boolean success, String code, String message,
        List<MobileFieldError> fieldErrors, Instant timestamp, String path, String traceId) {
    public static MobileApiErrorResponse of(String code, String message,
            List<MobileFieldError> errors, String path) {
        return new MobileApiErrorResponse(false, code, message, errors, Instant.now(), path, null);
    }
}
