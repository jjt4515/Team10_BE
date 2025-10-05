package poomasi.domain.aftersales.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poomasi.domain.aftersales.entity.FarmAfterSales;

public interface FarmAfterSalesRepository extends JpaRepository<FarmAfterSales, Long> {
}
