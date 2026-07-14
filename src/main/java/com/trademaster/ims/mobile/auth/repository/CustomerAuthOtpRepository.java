package com.trademaster.ims.mobile.auth.repository;
import com.trademaster.ims.mobile.auth.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CustomerAuthOtpRepository extends JpaRepository<CustomerAuthOtp,Long>{Optional<CustomerAuthOtp> findTopByAccountIdAndPurposeOrderByCreatedAtDesc(Long accountId,CustomerAuthOtp.Purpose purpose); List<CustomerAuthOtp> findByAccountIdAndPurposeAndConsumedAtIsNull(Long accountId,CustomerAuthOtp.Purpose purpose);}
