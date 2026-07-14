package com.trademaster.ims.mobile.auth.repository;
import com.trademaster.ims.mobile.auth.model.CustomerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CustomerAccountRepository extends JpaRepository<CustomerAccount,Long>{Optional<CustomerAccount> findByEmailIgnoreCase(String email); boolean existsByCustomerCustomerId(Long customerId);}
