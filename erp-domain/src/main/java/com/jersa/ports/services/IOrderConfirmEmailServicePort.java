package com.jersa.ports.services;

import com.jersa.entities.order.ROrderId;
import com.jersa.shared.REmail;
import com.jersa.shared.RMoney;

public interface IOrderConfirmEmailServicePort {
    void sendEmail(REmail email, ROrderId orderId, String orderNumber, RMoney money, String customerName, Integer itemsCount);
}
