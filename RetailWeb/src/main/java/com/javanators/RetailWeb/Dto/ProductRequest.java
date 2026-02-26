package com.javanators.RetailWeb.Dto;


import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    private String name;
    private Long categoryId;
    private String brand;
    private BigDecimal price;
    private Integer stock;
    private String packaging;
    private String imageUrl;
}
