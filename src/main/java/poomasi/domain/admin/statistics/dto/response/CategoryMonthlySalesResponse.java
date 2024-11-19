package poomasi.domain.admin.statistics.dto.response;

import java.math.BigDecimal;

public record CategoryMonthlySalesResponse(
        String categoryName,
        int count,
        BigDecimal totalSales
) { }
