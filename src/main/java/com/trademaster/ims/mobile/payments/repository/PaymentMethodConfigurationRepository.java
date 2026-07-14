package com.trademaster.ims.mobile.payments.repository;
import com.trademaster.ims.mobile.payments.model.PaymentMethodConfiguration;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface PaymentMethodConfigurationRepository extends JpaRepository<PaymentMethodConfiguration,Long>{Optional<PaymentMethodConfiguration>findByCode(String code);List<PaymentMethodConfiguration>findByActiveTrueOrderByIdAsc();boolean existsByCode(String code);}
