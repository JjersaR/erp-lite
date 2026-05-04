package com.jersa.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jersa.persistence.jpa.entities.OrderEntity;

@Repository
public interface IOrderEntityRepository extends JpaRepository<OrderEntity, UUID> {
    Optional<OrderEntity> findByOrderNumber(String orderNumber);

    List<OrderEntity> findByCustomerId(Long customerId);
}
