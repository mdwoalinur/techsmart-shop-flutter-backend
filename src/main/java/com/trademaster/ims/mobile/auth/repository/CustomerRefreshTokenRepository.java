package com.trademaster.ims.mobile.auth.repository;
import com.trademaster.ims.mobile.auth.model.CustomerRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CustomerRefreshTokenRepository extends JpaRepository<CustomerRefreshToken,Long>{Optional<CustomerRefreshToken> findByTokenHash(String hash); List<CustomerRefreshToken> findByTokenFamilyIdAndRevokedAtIsNull(String family); List<CustomerRefreshToken> findByAccountIdAndRevokedAtIsNull(Long accountId);}
