package com.buy01.order.dto;

import lombok.Data;
import java.util.List;

@Data
public class CartDTO {
    private String userId;
    private List<CartItemDTO> items;
}
