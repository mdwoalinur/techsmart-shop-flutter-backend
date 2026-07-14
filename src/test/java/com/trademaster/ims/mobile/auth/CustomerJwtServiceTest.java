package com.trademaster.ims.mobile.auth;
import com.trademaster.ims.mobile.auth.security.CustomerJwtService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
class CustomerJwtServiceTest {@Test void tokenIsCustomerAudienceAndShortLived(){CustomerJwtService s=new CustomerJwtService();ReflectionTestUtils.setField(s,"secret","0123456789012345678901234567890123456789012345678901234567890123");ReflectionTestUtils.setField(s,"accessSeconds",900L);var token=s.issue(42L);assertEquals(42L,s.parseCustomerAccountId(token.token()));assertTrue(token.expiresAt().isBefore(java.time.Instant.now().plusSeconds(901)));}}
