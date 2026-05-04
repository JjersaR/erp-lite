package com.jersa.ports.repositories;

import com.jersa.entities.order.OrderRoot;
import com.jersa.entities.order.ROrderId;
import com.jersa.entities.order.ROrderNumber;
import com.jersa.shared.RCustomerId;

import java.util.List;
import java.util.Optional;

public interface IOrderRepositoryPort {
    OrderRoot save(OrderRoot orderRoot);

    Optional<OrderRoot> findById(ROrderId orderId);

    Optional<OrderRoot> findByOrderNumber(ROrderNumber orderNumber);

    List<OrderRoot> findByCustomerId(RCustomerId customerId);

    void delete(OrderRoot orderRoot);
}
