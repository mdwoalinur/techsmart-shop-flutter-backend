package com.trademaster.ims.mobile.shopping.repository;
import com.trademaster.ims.mobile.shopping.model.CustomerWishlistItem; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface CustomerWishlistItemRepository extends JpaRepository<CustomerWishlistItem,Long>{List<CustomerWishlistItem> findByWishlistIdOrderByIdAsc(Long id); boolean existsByWishlistIdAndProductId(Long wishlistId,Long productId); void deleteByWishlistIdAndProductId(Long wishlistId,Long productId); void deleteByWishlistId(Long wishlistId);}
