package com.trademaster.ims.mobile.catalog.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.Set;

@Service
public class MobileImageUrlService {
    private static final Set<String> PUBLIC_PREFIXES = Set.of("/uploads/products/", "/uploads/product-variations/");
    private final String publicBaseUrl;

    public MobileImageUrlService(@Value("${app.mobile.public-base-url:}") String publicBaseUrl) {
        this.publicBaseUrl = trimTrailingSlash(publicBaseUrl == null ? "" : publicBaseUrl.trim());
    }

    public String resolve(String imageUrl, HttpServletRequest request) {
        if (imageUrl == null || imageUrl.isBlank()) return null;
        String value = imageUrl.trim();
        if (isSafeAbsolute(value)) return value;
        if (!value.startsWith("/") || PUBLIC_PREFIXES.stream().noneMatch(value::startsWith)) return null;
        String base = publicBaseUrl.isBlank()
                ? UriComponentsBuilder.newInstance().scheme(request.getScheme()).host(request.getServerName())
                    .port(request.getServerPort()).build().toUriString()
                : publicBaseUrl;
        return base + value;
    }

    private boolean isSafeAbsolute(String value) {
        try {
            URI uri = URI.create(value);
            return uri.isAbsolute() && ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private String trimTrailingSlash(String value) { return value.replaceFirst("/+$", ""); }
}
