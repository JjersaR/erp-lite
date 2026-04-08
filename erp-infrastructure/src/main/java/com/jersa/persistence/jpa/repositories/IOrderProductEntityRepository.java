package com.jersa.persistence.jpa.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jersa.persistence.jpa.entities.OrderProductEntity;

@Repository
public interface IOrderProductEntityRepository extends JpaRepository<OrderProductEntity, UUID> {

}
