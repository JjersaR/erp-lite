package com.jersa.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot<ID> extends Entity<ID> {

  private final List<IDomainEvent> domainEvents = new ArrayList<>();

  protected AggregateRoot(ID id) {
    super(id);
  }

  /**
   * Registers a domain event to be published.
   *
   * @param event the domain event to register
   */
  protected void registerEvent(IDomainEvent event) {
    if (event == null) {
      throw new IllegalArgumentException("Domain event cannot be null");
    }
    this.domainEvents.add(event);
  }

  /**
   * Returns all domain events and clears the internal list.
   * This method should be called by the infrastructure layer after persisting the
   * aggregate.
   *
   * @return an unmodifiable list of domain events
   */
  public List<IDomainEvent> getDomainEvents() {
    return Collections.unmodifiableList(domainEvents);
  }

  /**
   * Clears all domain events.
   * This method should be called by the infrastructure layer after publishing
   * events.
   */
  public void clearDomainEvents() {
    this.domainEvents.clear();
  }

  /**
   * Returns true if there are pending domain events.
   */
  public boolean hasDomainEvents() {
    return !domainEvents.isEmpty();
  }
}
