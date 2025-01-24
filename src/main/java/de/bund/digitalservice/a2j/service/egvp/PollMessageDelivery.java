package de.bund.digitalservice.a2j.service.egvp;

import de.bund.digitalservice.a2j.repository.egvp.MessageInTransit;
import de.bund.digitalservice.a2j.repository.egvp.MessagesInTransitRepository;
import de.bund.digitalservice.a2j.service.egvp.client.EgvpClient;
import de.bund.digitalservice.a2j.service.egvp.client.EgvpClientException;
import de.bund.digitalservice.a2j.service.egvp.client.MessageDeliveryStatusResponse;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Configuration
@EnableScheduling
@ConditionalOnProperty("egvp.pollDeliveryStatus")
public class PollMessageDelivery {

  private final EgvpClient client;

  private final MessagesInTransitRepository repository;

  private static final Logger logger = LoggerFactory.getLogger(PollMessageDelivery.class);

  public PollMessageDelivery(EgvpClient client, MessagesInTransitRepository repository) {
    this.client = client;
    this.repository = repository;
  }

  @Scheduled(fixedRate = 5000)
  public void retrieveDeliveryStatus() {
    logger.info("POLLING MESSAGE DELIVERY");
    Set<MessageInTransit> deliveredMessages = new HashSet<>();
    Set<MessageInTransit> failedMessages = new HashSet<>();

    Set<MessageInTransit> inTransit = repository.getAll();

    inTransit.forEach(
        m -> {
          logger.info("checking status for message with customId {}", m.customId());
          try {
            MessageDeliveryStatusResponse response =
                this.client.checkMessageStatus(m.userId(), m.customId());

            if (response.delivered()) {
              deliveredMessages.add(m);
              // TODO send confirmation file to user
            } else {
              logger.info(
                  "message {} with customId {} still in transit : {}",
                  response.messageId(),
                  m.customId(),
                  response.pendingReason());
            }
          } catch (EgvpClientException e) {
            failedMessages.add(m);
            // TODO send failure to user
            logger.error("Unable to deliver message :{}", e.getMessage());
          }
        });
    repository.removeAll(
        Stream.concat(deliveredMessages.stream(), failedMessages.stream())
            .collect(Collectors.toSet()));
  }
}
