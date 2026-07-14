package com.trademaster.ims.mobile.payments.repository;
import com.trademaster.ims.mobile.payments.model.MobileWalletProviderConfiguration;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface MobileWalletProviderConfigurationRepository extends JpaRepository<MobileWalletProviderConfiguration,Long>{Optional<MobileWalletProviderConfiguration>findByCode(String code);List<MobileWalletProviderConfiguration>findByActiveTrueOrderByDisplayOrderAscCodeAsc();boolean existsByCode(String code);}
