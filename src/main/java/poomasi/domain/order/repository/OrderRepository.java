package poomasi.domain.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poomasi.domain.order.entity.Order;
import poomasi.domain.statistics.dto.response.CategoryMonthlySalesResponse;
import poomasi.domain.statistics.dto.response.StoreMonthlySalesResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByMerchantUid(String merchantUid);
    List<Order> findByMemberId(Long memberId);
    //Page<Order> findByMemberId(Long memberId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.updateAt BETWEEN :startDate AND :endDate")
    List<Order> findAllByUpdateAtBetween(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    @Query(value = """
        SELECT 
            :storeId as storeId,
            DATE_FORMAT(o.updated_at, '%Y-%m') as month,
            SUM(op.price * op.count) as totalSales
        FROM ordered_products op
        JOIN product_order o ON op.product_order_id = o.id
        JOIN product p ON op.product_id = p.id
        WHERE p.store_id = :storeId
          AND o.updated_at BETWEEN :startDate AND :endDate
          AND op.ordered_product_status = 'DELIVERED'
        GROUP BY DATE_FORMAT(o.updated_at, '%Y-%m')
        ORDER BY month
    """, nativeQuery = true)
    List<Map<String, Object>> findMonthlyStoreSalesOptimized(
            @Param("storeId") Long storeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query(value = """
        SELECT 
            :storeId as storeId,
            p.category_id as categoryId,
            SUM(op.price * op.count) as totalSales
        FROM ordered_products op
        JOIN product_order o ON op.product_order_id = o.id
        JOIN product p ON op.product_id = p.id
        WHERE p.store_id = :storeId
          AND o.updated_at BETWEEN :startDate AND :endDate
          AND op.ordered_product_status = 'DELIVERED'
        GROUP BY p.category_id
        ORDER BY totalSales DESC
    """, nativeQuery = true)
    List<Map<String, Object>> findCategorySalesOptimized(
            @Param("storeId") Long storeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
