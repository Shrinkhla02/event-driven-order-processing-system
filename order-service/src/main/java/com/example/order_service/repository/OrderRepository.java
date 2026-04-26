package com.example.order_service.repository;

import com.example.order_service.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,Long> {

    public Optional<OrderEntity> findByUserIdAndOrderId(Long orderId, Long UserId);

    public List<OrderEntity> findByUserId(Long userId);

    public void deleteByUserIdAndOrderId(Long userId, Long orderId);
}
