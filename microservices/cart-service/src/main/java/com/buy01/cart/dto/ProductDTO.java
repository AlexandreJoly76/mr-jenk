package com.buy01.cart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private String id;
    private String name;
    private BigDecimal price;
    private Integer quantity; // available stock
    
    @JsonProperty("userId")
    private String userId; 
}
