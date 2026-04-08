package com.jersa.persistence.jpa.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jersa.persistence.jpa.entities.ProductEntity;

@Repository
public interface IProductEntityRepository extends JpaRepository<ProductEntity, UUID> {

}
