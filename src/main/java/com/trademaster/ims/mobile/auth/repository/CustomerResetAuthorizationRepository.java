package com.trademaster.ims.mobile.auth.repository;
import com.trademaster.ims.mobile.auth.model.CustomerResetAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CustomerResetAuthorizationRepository extends JpaRepository<CustomerResetAuthorization,Long>{Optional<CustomerResetAuthorization> findByTokenHash(String hash);}
