package com.trademaster.ims.mobile.shopping.repository;
import com.trademaster.ims.mobile.shopping.model.CustomerCart; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface CustomerCartRepository extends JpaRepository<CustomerCart,Long>{Optional<CustomerCart> findByCustomerAccountIdAndStatus(Long accountId,CustomerCart.Status status);}
