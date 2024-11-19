package poomasi.domain.admin.statistics.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CategoryMonthlySalesResponse {
    private String categoryName;
    private int count;
    private BigDecimal totalSales;

    public CategoryMonthlySalesResponse(String categoryName, int count, BigDecimal totalSales) {
        this.categoryName = categoryName;
        this.count = count;
        this.totalSales = totalSales;
    }
}