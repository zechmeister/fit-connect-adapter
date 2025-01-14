package de.bund.digitalservice.a2j.repository.egvp;

import java.util.Set;

public interface MessagesInTransitRepository {

  public void add(MessageInTransit message);

  public void removeAll(Set<MessageInTransit> messages);

  public Set<MessageInTransit> getAll();
}
