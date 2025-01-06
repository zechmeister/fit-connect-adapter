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
public class EgvpOutboxServiceTest {

  @MockBean private EgvpClient egvpClient;

  @Test
  public void sendMessageTest() throws EgvpClientException {
    MessagesInTransitRepository repo = new MessagesInTransitInMemoryRepository();
    EgvpOutboxService service = new EgvpOutboxService(egvpClient, repo);

    SendMessageRequest request =
        new SendMessageRequest(
            "userId",
            "receiverId",
            "mailBoxId",
            "testSubject",
            "attachmentPath",
            "xJustizFilePath");
    String expectedCustomId = "newCustomId";
    when(this.egvpClient.sendMessage(request))
        .thenReturn(new SendMessageResponse(expectedCustomId));

    Assertions.assertEquals(expectedCustomId, service.sendMessage(request));
    Assertions.assertTrue(
        repo.getAll().contains(new MessageInTransit("userId", expectedCustomId, "mailBoxId")));
  }

  @Test
  public void retrieveDeliveredMessage() throws EgvpClientException {
    MessagesInTransitRepository repo = new MessagesInTransitInMemoryRepository();
    repo.add(new MessageInTransit("userId", "customId", "mailboxId"));

    EgvpOutboxService service = new EgvpOutboxService(egvpClient, repo);
    when(egvpClient.checkMessageStatus("userId", "customId"))
        .thenReturn(
            new MessageDeliveryStatusResponse("messageId", true, "", "pathToConfirmation.zip"));
    service.retrieveDeliveryStatus();

    Assertions.assertTrue(repo.getAll().isEmpty());
  }

  @Test
  public void keepPendingMessagesInTransit() throws EgvpClientException {
    MessagesInTransitRepository repo = new MessagesInTransitInMemoryRepository();
    MessageInTransit msg = new MessageInTransit("userId", "customId", "mailboxId");
    repo.add(msg);

    EgvpOutboxService service = new EgvpOutboxService(egvpClient, repo);
    when(egvpClient.checkMessageStatus("userId", "customId"))
        .thenReturn(new MessageDeliveryStatusResponse("", false, "WS_UNPROCESSABLE", ""));
    service.retrieveDeliveryStatus();

    Assertions.assertTrue(repo.getAll().contains(msg));
  }

  @Test
  public void deleteInTransitMessagesOnFailure() throws EgvpClientException {
    MessagesInTransitRepository repo = new MessagesInTransitInMemoryRepository();
    MessageInTransit msg = new MessageInTransit("userId", "customId", "mailboxId");
    repo.add(msg);

    EgvpOutboxService service = new EgvpOutboxService(egvpClient, repo);
    when(egvpClient.checkMessageStatus("userId", "customId"))
        .thenThrow(new EgvpClientException("GENERAL_ERROR"));

    service.retrieveDeliveryStatus();
    Assertions.assertTrue(repo.getAll().isEmpty());
  }
}
