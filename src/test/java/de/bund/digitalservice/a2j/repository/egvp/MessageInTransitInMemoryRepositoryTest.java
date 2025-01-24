package de.bund.digitalservice.a2j.repository.egvp;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class MessageInTransitInMemoryRepositoryTest {

  MessagesInTransitInMemoryRepository repo;

  @BeforeEach
  void setup() {
    repo = new MessagesInTransitInMemoryRepository();
  }

  @Test
  void deleteASpecificMessage() {
    MessageInTransit messageToBeDeleted = new MessageInTransit("userId", "customId2", "mailboxId");

    List.of(
            new MessageInTransit("userId", "customId", "mailboxId"),
            new MessageInTransit("userId", "customId3", "mailboxId"))
        .forEach(m -> repo.add(m));

    repo.removeAll(Set.of(messageToBeDeleted));

    Assertions.assertFalse(repo.getAll().stream().anyMatch(m -> m.customId().equals("customId2")));
    Assertions.assertEquals(2, repo.getAll().size());
  }
}
