package de.bund.digitalservice.a2j.repository.egvp;

import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class MessagesInTransitInMemoryRepository implements MessagesInTransitRepository {
  private final Set<MessageInTransit> messages = new HashSet<>();

  public Set<MessageInTransit> getAll() {
    return messages;
  }

  @Override
  public void add(MessageInTransit message) {
    this.messages.add(message);
  }

  @Override
  public void removeAll(Set<MessageInTransit> messages) {
    this.messages.removeAll(messages);
  }
}
