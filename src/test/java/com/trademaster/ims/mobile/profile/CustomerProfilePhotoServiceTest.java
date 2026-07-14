package com.trademaster.ims.mobile.profile;

import com.trademaster.ims.mobile.auth.model.CustomerAccount;
import com.trademaster.ims.mobile.auth.repository.CustomerAccountRepository;
import com.trademaster.ims.mobile.auth.service.CustomerAuthException;
import com.trademaster.ims.mobile.profile.service.CustomerProfilePhotoService;
import com.trademaster.ims.model.Customer;
import com.trademaster.ims.repository.CustomerRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

class CustomerProfilePhotoServiceTest {
    @Mock CustomerAccountRepository accounts;
    @Mock CustomerRepository customers;
    AutoCloseable mocks;
    CustomerProfilePhotoService service;
    CustomerAccount account;
    Customer customer;
    @TempDir Path uploads;

    @BeforeEach void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        service = new CustomerProfilePhotoService(accounts, customers, uploads.toString());
        customer = new Customer("CUST-7", "Test Customer", "test@example.com", "01712345678");
        customer.setCustomerId(101L);
        account = new CustomerAccount();
        account.setId(7L);
        account.setCustomer(customer);
        account.setEmail("test@example.com");
        account.setStatus(CustomerAccount.Status.ACTIVE);
        account.setEmailVerified(true);
        when(accounts.findById(7L)).thenReturn(Optional.of(account));
        when(customers.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    @AfterEach void tearDown() throws Exception {
        mocks.close();
    }

    @Test void uploadStoresPhotoUrlAndReturnsUpdatedProfile() throws Exception {
        var response = service.upload(7L, jpeg("avatar.jpg", 512));
        assertEquals(101L, response.customerId());
        assertEquals(customer.getPhotoUrl(), response.photoUrl());
        assertTrue(response.photoUrl().startsWith("/uploads/customers/profile/7/customer-profile-"));
        String filename = response.photoUrl().substring("/uploads/customers/profile/7/".length());
        assertTrue(Files.exists(uploads.resolve("profile").resolve("7").resolve(filename)));
        verify(customers).save(same(customer));
    }

    @Test void invalidTypeIsRejected() {
        MockMultipartFile file = new MockMultipartFile("photo", "avatar.gif", "image/gif", new byte[]{'G', 'I', 'F'});
        CustomerAuthException ex = assertThrows(CustomerAuthException.class, () -> service.upload(7L, file));
        assertEquals("INVALID_PROFILE_PHOTO_TYPE", ex.code());
        verify(customers, never()).save(any());
    }

    @Test void oversizedFileIsRejected() {
        byte[] large = new byte[(2 * 1024 * 1024) + 1];
        large[0] = (byte) 0xFF;
        large[1] = (byte) 0xD8;
        large[2] = (byte) 0xFF;
        MockMultipartFile file = new MockMultipartFile("photo", "avatar.jpg", "image/jpeg", large);
        CustomerAuthException ex = assertThrows(CustomerAuthException.class, () -> service.upload(7L, file));
        assertEquals("PROFILE_PHOTO_TOO_LARGE", ex.code());
        verify(customers, never()).save(any());
    }

    @Test void uploadUsesJwtAccountOnlySoCustomerBCannotBeTargeted() {
        Customer b = new Customer("CUST-8", "Other Customer", "other@example.com", "01700000000");
        b.setCustomerId(202L);
        service.upload(7L, jpeg("avatar.jpg", 64));
        verify(accounts).findById(7L);
        verify(customers).save(same(customer));
        assertNull(b.getPhotoUrl());
    }

    private MockMultipartFile jpeg(String name, int size) {
        byte[] bytes = new byte[Math.max(size, 12)];
        bytes[0] = (byte) 0xFF;
        bytes[1] = (byte) 0xD8;
        bytes[2] = (byte) 0xFF;
        return new MockMultipartFile("photo", name, "image/jpeg", bytes);
    }
}