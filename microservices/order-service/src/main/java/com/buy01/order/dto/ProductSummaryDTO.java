package com.buy01.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSummaryDTO {
    private String productId;
    private String productName;
    private long count;
}
