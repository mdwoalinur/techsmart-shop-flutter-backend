package com.trademaster.ims.mobile.profile;

import com.trademaster.ims.config.SecurityConfig;
import com.trademaster.ims.mobile.auth.dto.CustomerAuthDtos.CustomerProfileResponse;
import com.trademaster.ims.mobile.auth.security.CustomerAuthTokenFilter;
import com.trademaster.ims.mobile.auth.security.CustomerAuthentication;
import com.trademaster.ims.mobile.common.exception.MobileExceptionHandler;
import com.trademaster.ims.mobile.profile.controller.CustomerProfileController;
import com.trademaster.ims.mobile.profile.service.CustomerProfilePhotoService;
import com.trademaster.ims.security.JwtUtils;
import com.trademaster.ims.security.UserDetailsServiceImpl;
import jakarta.servlet.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerProfileController.class)
@Import({SecurityConfig.class, MobileExceptionHandler.class})
@ContextConfiguration(classes = {CustomerProfileController.class, SecurityConfig.class, MobileExceptionHandler.class})
class CustomerProfilePhotoSecurityTest {
    @Autowired MockMvc mvc;
    @MockitoBean CustomerProfilePhotoService service;
    @MockitoBean UserDetailsServiceImpl users;
    @MockitoBean JwtUtils jwtUtils;
    @MockitoBean CustomerAuthTokenFilter customerFilter;

    @BeforeEach void passFilter() throws Exception {
        doAnswer(i -> {
            ((FilterChain) i.getArgument(2)).doFilter((ServletRequest) i.getArgument(0), (ServletResponse) i.getArgument(1));
            return null;
        }).when(customerFilter).doFilter(any(), any(), any());
        when(service.upload(eq(7L), any())).thenReturn(new CustomerProfileResponse(
                101L, "CUST-7", "Test Customer", "test@example.com", "01712345678", "RETAIL",
                null, null, null, null, null, "/uploads/customers/profile/7/avatar.jpg", true));
    }

    @Test void anonymousUploadDenied() throws Exception {
        mvc.perform(multipart("/api/mobile/v1/profile/photo").file(photo()))
                .andExpect(status().isUnauthorized());
        verify(service, never()).upload(anyLong(), any());
    }

    @Test void customerPrincipalOwnsPhotoUploadEvenIfRequestSuppliesCustomerId() throws Exception {
        mvc.perform(multipart("/api/mobile/v1/profile/photo")
                        .file(photo())
                        .param("customerId", "999")
                        .with(authentication(new CustomerAuthentication(7L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.customerId").value(101))
                .andExpect(jsonPath("$.data.photoUrl").value("/uploads/customers/profile/7/avatar.jpg"));
        verify(service).upload(eq(7L), any());
    }

    private MockMultipartFile photo() {
        return new MockMultipartFile("photo", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE,
                new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    }
}