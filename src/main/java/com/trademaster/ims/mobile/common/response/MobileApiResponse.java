package com.trademaster.ims.mobile.common.response;

import java.time.Instant;

public record MobileApiResponse<T>(boolean success, T data, String message, Instant timestamp) {
    public static <T> MobileApiResponse<T> success(T data) {
        return new MobileApiResponse<>(true, data, null, Instant.now());
    }
}
