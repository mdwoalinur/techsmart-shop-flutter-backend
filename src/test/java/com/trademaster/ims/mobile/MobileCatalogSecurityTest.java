package com.trademaster.ims.mobile;

import com.trademaster.ims.config.SecurityConfig;
import com.trademaster.ims.config.DataInitializer;
import com.trademaster.ims.config.NotificationSchemaFixer;
import com.trademaster.ims.config.PaymentSchemaFixer;
import com.trademaster.ims.config.StockMovementSchemaFixer;
import com.trademaster.ims.mobile.catalog.controller.MobileCatalogController;
import com.trademaster.ims.mobile.auth.security.CustomerAuthTokenFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import com.trademaster.ims.mobile.catalog.controller.MobileHealthController;
import com.trademaster.ims.mobile.catalog.service.MobileCatalogService;
import com.trademaster.ims.mobile.common.exception.MobileExceptionHandler;
import com.trademaster.ims.mobile.common.response.MobileFieldError;
import com.trademaster.ims.mobile.common.exception.MobileValidationException;
import com.trademaster.ims.security.JwtUtils;
import com.trademaster.ims.security.UserDetailsServiceImpl;
import com.trademaster.ims.service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {MobileHealthController.class, MobileCatalogController.class})
@Import({SecurityConfig.class, MobileExceptionHandler.class})
@ContextConfiguration(classes = {MobileHealthController.class, MobileCatalogController.class,
        SecurityConfig.class, MobileExceptionHandler.class})
class MobileCatalogSecurityTest {
    @Autowired MockMvc mvc;
    @MockitoBean MobileCatalogService catalog;
    @MockitoBean CustomerAuthTokenFilter customerFilter;
    @MockitoBean UserDetailsServiceImpl users;
    @MockitoBean JwtUtils jwtUtils;
    @MockitoBean AuditLogService auditLogService;
    @MockitoBean DataInitializer dataInitializer;
    @MockitoBean NotificationSchemaFixer notificationSchemaFixer;
    @MockitoBean PaymentSchemaFixer paymentSchemaFixer;
    @MockitoBean StockMovementSchemaFixer stockMovementSchemaFixer;

    @BeforeEach void passCustomerFilter() throws Exception {
        doAnswer(invocation -> {
            ((FilterChain) invocation.getArgument(2)).doFilter(
                    (ServletRequest) invocation.getArgument(0),
                    (ServletResponse) invocation.getArgument(1));
            return null;
        }).when(customerFilter).doFilter(any(), any(), any());
    }

    @Test void healthIsPublic() throws Exception {
        mvc.perform(get("/api/mobile/v1/health"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("UP"));
    }

    @Test void categoriesArePublic() throws Exception {
        when(catalog.categories(null, false)).thenReturn(List.of());
        mvc.perform(get("/api/mobile/v1/categories"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").isArray());
    }

    @Test void productsArePublic() throws Exception {
        when(catalog.products(any(), any())).thenReturn(Page.empty());
        mvc.perform(get("/api/mobile/v1/products"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.page").value(0));
    }

    @Test void categoryProductsArePublic() throws Exception {
        when(catalog.products(any(), any())).thenReturn(Page.empty());
        mvc.perform(get("/api/mobile/v1/categories/1/products"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
    }

    @Test void existingBusinessApiRemainsProtected() throws Exception {
        mvc.perform(get("/api/products")).andExpect(status().isUnauthorized());
    }

    @Test void mobilePostIsDenied() throws Exception {
        mvc.perform(post("/api/mobile/v1/products")).andExpect(status().isUnauthorized());
    }

    @Test void validationUsesStableEnvelope() throws Exception {
        when(catalog.products(any(), any())).thenThrow(
                new MobileValidationException(List.of(new MobileFieldError("size", "Size must be between 1 and 100."))));
        mvc.perform(get("/api/mobile/v1/products").param("size", "101"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("size"));
    }
}

