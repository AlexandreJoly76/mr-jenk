package com.buy01.order.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemDTO {
    private String productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private String sellerId;
}
