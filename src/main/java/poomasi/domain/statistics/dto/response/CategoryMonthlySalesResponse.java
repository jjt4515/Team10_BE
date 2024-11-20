package poomasi.domain.statistics.dto.response;


import java.math.BigDecimal;

public record CategoryMonthlySalesResponse (
        Long storeId,
        Long categoryId,
        BigDecimal totalSales) {

}