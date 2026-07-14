package com.trademaster.ims.mobile.profile.service;

import com.trademaster.ims.mobile.auth.dto.CustomerAuthDtos.CustomerProfileResponse;
import com.trademaster.ims.mobile.auth.model.CustomerAccount;
import com.trademaster.ims.mobile.auth.repository.CustomerAccountRepository;
import com.trademaster.ims.mobile.auth.service.CustomerAuthException;
import com.trademaster.ims.model.Customer;
import com.trademaster.ims.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class CustomerProfilePhotoService {
    private static final long MAX_PROFILE_PHOTO_SIZE = 2 * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final CustomerAccountRepository accounts;
    private final CustomerRepository customers;
    private final Path uploadRoot;

    public CustomerProfilePhotoService(
            CustomerAccountRepository accounts,
            CustomerRepository customers,
            @Value("${techsmart.mobile.profile-photo.upload-dir:uploads/customers}") String uploadDir) {
        this.accounts = accounts;
        this.customers = customers;
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Transactional
    public CustomerProfileResponse upload(Long accountId, MultipartFile photo) {
        CustomerAccount account = accounts.findById(accountId)
                .orElseThrow(() -> error(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_REQUIRED", "Authentication is required."));
        if (account.getStatus() != CustomerAccount.Status.ACTIVE || !account.isEmailVerified()) {
            throw error(HttpStatus.FORBIDDEN, "ACCOUNT_NOT_ACTIVE", "Your account is not active.");
        }
        Customer customer = account.getCustomer();
        String oldPhotoUrl = customer.getPhotoUrl();
        String nextPhotoUrl = store(accountId, photo);
        customer.setPhotoUrl(nextPhotoUrl);
        customers.save(customer);
        deleteOldPhotoIfOwned(accountId, oldPhotoUrl, nextPhotoUrl);
        return profileOf(account);
    }

    private String store(Long accountId, MultipartFile photo) {
        Validation validation = validate(photo);
        Path accountDir = uploadRoot.resolve("profile").resolve(accountId.toString()).normalize();
        if (!accountDir.startsWith(uploadRoot)) {
            throw error(HttpStatus.BAD_REQUEST, "INVALID_PROFILE_PHOTO", "The selected photo could not be used.");
        }
        try {
            Files.createDirectories(accountDir);
            String filename = "customer-profile-" + UUID.randomUUID() + "." + validation.extension();
            Path target = accountDir.resolve(filename).normalize();
            if (!target.startsWith(accountDir)) {
                throw error(HttpStatus.BAD_REQUEST, "INVALID_PROFILE_PHOTO", "The selected photo could not be used.");
            }
            try (InputStream input = photo.getInputStream()) {
                Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return "/uploads/customers/profile/" + accountId + "/" + filename;
        } catch (IOException ex) {
            throw error(HttpStatus.INTERNAL_SERVER_ERROR, "PROFILE_PHOTO_STORAGE_FAILED", "The profile photo could not be saved.");
        }
    }

    private Validation validate(MultipartFile photo) {
        if (photo == null || photo.isEmpty()) {
            throw error(HttpStatus.BAD_REQUEST, "PROFILE_PHOTO_REQUIRED", "Choose a profile photo to upload.");
        }
        if (photo.getSize() > MAX_PROFILE_PHOTO_SIZE) {
            throw error(HttpStatus.PAYLOAD_TOO_LARGE, "PROFILE_PHOTO_TOO_LARGE", "Profile photo must be 2 MB or smaller.");
        }
        String original = StringUtils.cleanPath(photo.getOriginalFilename() == null ? "" : photo.getOriginalFilename());
        if (!StringUtils.hasText(original)
                || original.contains("..")
                || original.contains("/")
                || original.contains("\\")
                || original.matches("(?i).*[.]((exe)|(bat)|(cmd)|(sh)|(js)|(jar)|(php)|(html?))$")) {
            throw error(HttpStatus.BAD_REQUEST, "INVALID_PROFILE_PHOTO", "The selected photo filename is not allowed.");
        }
        String extension = extension(original);
        String contentType = photo.getContentType() == null ? "" : photo.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(extension) || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw error(HttpStatus.BAD_REQUEST, "INVALID_PROFILE_PHOTO_TYPE", "Only JPG, PNG, and WEBP photos are allowed.");
        }
        try {
            byte[] bytes = photo.getBytes();
            if (!matchesImageSignature(bytes, extension)) {
                throw error(HttpStatus.BAD_REQUEST, "INVALID_PROFILE_PHOTO_TYPE", "Only valid JPG, PNG, and WEBP photos are allowed.");
            }
        } catch (IOException ex) {
            throw error(HttpStatus.BAD_REQUEST, "INVALID_PROFILE_PHOTO", "The selected photo could not be read.");
        }
        return new Validation(extension);
    }

    private boolean matchesImageSignature(byte[] bytes, String extension) {
        if (bytes == null || bytes.length < 12) {
            return false;
        }
        if (extension.equals("jpg") || extension.equals("jpeg")) {
            return bytes.length >= 3
                    && (bytes[0] & 0xFF) == 0xFF
                    && (bytes[1] & 0xFF) == 0xD8
                    && (bytes[2] & 0xFF) == 0xFF;
        }
        if (extension.equals("png")) {
            return bytes.length >= 8
                    && (bytes[0] & 0xFF) == 0x89
                    && bytes[1] == 0x50
                    && bytes[2] == 0x4E
                    && bytes[3] == 0x47
                    && bytes[4] == 0x0D
                    && bytes[5] == 0x0A
                    && bytes[6] == 0x1A
                    && bytes[7] == 0x0A;
        }
        return bytes[0] == 0x52
                && bytes[1] == 0x49
                && bytes[2] == 0x46
                && bytes[3] == 0x46
                && bytes[8] == 0x57
                && bytes[9] == 0x45
                && bytes[10] == 0x42
                && bytes[11] == 0x50;
    }

    private String extension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            throw error(HttpStatus.BAD_REQUEST, "INVALID_PROFILE_PHOTO", "Profile photo file extension is required.");
        }
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private void deleteOldPhotoIfOwned(Long accountId, String oldPhotoUrl, String nextPhotoUrl) {
        String prefix = "/uploads/customers/profile/" + accountId + "/";
        if (!StringUtils.hasText(oldPhotoUrl) || oldPhotoUrl.equals(nextPhotoUrl) || !oldPhotoUrl.startsWith(prefix)) {
            return;
        }
        String filename = oldPhotoUrl.substring(prefix.length());
        Path accountDir = uploadRoot.resolve("profile").resolve(accountId.toString()).normalize();
        Path target = accountDir.resolve(filename).normalize();
        if (!target.startsWith(accountDir)) {
            return;
        }
        try {
            Files.deleteIfExists(target);
        } catch (IOException ignored) {
            // Replacing the avatar should not fail only because an old owned file could not be removed.
        }
    }

    private CustomerProfileResponse profileOf(CustomerAccount account) {
        Customer customer = account.getCustomer();
        return new CustomerProfileResponse(
                customer.getCustomerId(),
                customer.getCustomerCode(),
                customer.getCustomerName(),
                account.getEmail(),
                customer.getPhone(),
                customer.getCustomerType().name(),
                customer.getAddress(),
                customer.getCity(),
                customer.getState(),
                customer.getPostalCode(),
                customer.getCountry(),
                customer.getPhotoUrl(),
                account.isEmailVerified());
    }

    private CustomerAuthException error(HttpStatus status, String code, String message) {
        return new CustomerAuthException(status, code, message);
    }

    private record Validation(String extension) {
    }
}