package com.javanators.RetailWeb.Dto;

import lombok.Data;

@Data
public class OrderRequest {
    private String address;
    private String couponCode;
}