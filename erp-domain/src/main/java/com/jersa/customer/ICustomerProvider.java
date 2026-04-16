package com.jersa.customer;

import java.util.Optional;

/**
 * Port for external service for JSON
 */
public interface ICustomerProvider {

  Optional<RCustomerInfo> findById(Long id);

  boolean existsById(Long id);
}
