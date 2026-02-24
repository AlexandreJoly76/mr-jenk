package com.buy01.cart.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private String id;
    private String name;
    private BigDecimal price;
    private Integer quantity; // available stock
    private String userId; // added sellerId
}
