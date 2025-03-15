package com.czb.news.controller;


import lombok.Getter;
import lombok.Setter;

/**
 * 支付请求 DTO
 */
@Setter
@Getter
public class PaymentRequest {
    // Getters and Setters
    private double amount;
    private String paymentMethod;

}