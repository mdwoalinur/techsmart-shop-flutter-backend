package com.trademaster.ims.mobile.common.response;

import org.springframework.data.domain.Page;
import java.util.List;

public record MobilePageResponse<T>(List<T> content, int page, int size, long totalElements,
        int totalPages, boolean first, boolean last) {
    public static <T> MobilePageResponse<T> from(Page<T> page) {
        return new MobilePageResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }
}
