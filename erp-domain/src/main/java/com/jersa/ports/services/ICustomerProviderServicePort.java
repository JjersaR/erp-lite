package com.jersa.ports.services;

import com.jersa.entities.customer.RCustomerInfo;

import java.util.Optional;

/**
 * Port for external service for JSON
 */
public interface ICustomerProviderServicePort {

  Optional<RCustomerInfo> findById(Long id);

  boolean existsById(Long id);
}
