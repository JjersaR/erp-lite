package com.jersa.ports.messages;

import com.jersa.common.IDomainEvent;

public interface EventPublisherPort {
    void publish(IDomainEvent event);
}
