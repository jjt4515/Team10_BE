package poomasi.domain.admin.statistics.dto.response;

import poomasi.domain.store.entity.Store;

import java.math.BigDecimal;

public record StoreMonthlySalesResponse(
        Store store,
        BigDecimal totalSales
) {}
