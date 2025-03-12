package com.czb.news.repository;

import com.czb.news.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 支付记录数据访问接口
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
