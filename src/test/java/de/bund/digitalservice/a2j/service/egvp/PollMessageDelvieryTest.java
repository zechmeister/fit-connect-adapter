package de.bund.digitalservice.a2j.service.egvp;

import static org.mockito.Mockito.when;

import de.bund.digitalservice.a2j.repository.egvp.MessageInTransit;
import de.bund.digitalservice.a2j.repository.egvp.MessagesInTransitInMemoryRepository;
import de.bund.digitalservice.a2j.repository.egvp.MessagesInTransitRepository;
import de.bund.digitalservice.a2j.service.egvp.client.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class PollMessageDelvieryTest {

  @MockBean private EgvpClient egvpClient;

  @Test
  void retrieveDeliveredMessage() throws EgvpClientException {
    MessagesInTransitRepository repo = new MessagesInTransitInMemoryRepository();
    repo.add(new MessageInTransit("userId", "customId", "mailboxId"));

    PollMessageDelivery service = new PollMessageDelivery(egvpClient, repo);
    when(egvpClient.checkMessageStatus("userId", "customId"))
        .thenReturn(
            new MessageDeliveryStatusResponse("messageId", true, "", "pathToConfirmation.zip"));
    service.retrieveDeliveryStatus();

    Assertions.assertTrue(repo.getAll().isEmpty());
  }

  @Test
  void keepPendingMessagesInTransit() throws EgvpClientException {
    MessagesInTransitRepository repo = new MessagesInTransitInMemoryRepository();
    MessageInTransit msg = new MessageInTransit("userId", "customId", "mailboxId");
    repo.add(msg);

    PollMessageDelivery service = new PollMessageDelivery(egvpClient, repo);
    when(egvpClient.checkMessageStatus("userId", "customId"))
        .thenReturn(new MessageDeliveryStatusResponse("", false, "WS_UNPROCESSABLE", ""));
    service.retrieveDeliveryStatus();

    Assertions.assertTrue(repo.getAll().contains(msg));
  }

  @Test
  void deleteInTransitMessagesOnFailure() throws EgvpClientException {
    MessagesInTransitRepository repo = new MessagesInTransitInMemoryRepository();
    MessageInTransit msg = new MessageInTransit("userId", "customId", "mailboxId");
    repo.add(msg);

    PollMessageDelivery service = new PollMessageDelivery(egvpClient, repo);
    when(egvpClient.checkMessageStatus("userId", "customId"))
        .thenThrow(new EgvpClientException("GENERAL_ERROR"));

    service.retrieveDeliveryStatus();
    Assertions.assertTrue(repo.getAll().isEmpty());
  }
}
