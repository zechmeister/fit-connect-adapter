package de.bund.digitalservice.a2j.config;

import com.nimbusds.jose.jwk.JWK;
import dev.fitko.fitconnect.api.config.ApplicationConfig;
import dev.fitko.fitconnect.api.config.SenderConfig;
import dev.fitko.fitconnect.api.config.SubscriberConfig;
import dev.fitko.fitconnect.api.config.defaults.Environments;
import dev.fitko.fitconnect.client.SenderClient;
import dev.fitko.fitconnect.client.SubscriberClient;
import dev.fitko.fitconnect.client.bootstrap.ClientFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class FitConnectConfig {
  private final ResourceLoader resourceLoader;

  @Value("${fitConnect.sender.clientId}")
  String senderClientId;

  @Value("${fitConnect.sender.clientSecret}")
  String senderClientSecret;

  @Value("${fitConnect.subscriber.clientId}")
  String subscriberClientId;

  @Value("${fitConnect.subscriber.clientSecret}")
  String subscriberClientSecret;

  @Value("${fitConnect.subscriber.privateDecryptionKeyPath}")
  String subscriberPrivateDecryptionKeyPath;

  @Value("${fitConnect.subscriber.privateSigningKeyPath}")
  String subscriberPrivateSigningKeyPath;

  public FitConnectConfig(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  @Bean
  public SenderClient senderClient() {
    SenderConfig senderConfig =
        SenderConfig.builder().clientId(senderClientId).clientSecret(senderClientSecret).build();

    ApplicationConfig config =
        ApplicationConfig.builder()
            .senderConfig(senderConfig)
            .activeEnvironment(Environments.TEST.getEnvironmentName())
            .build();

    return ClientFactory.createSenderClient(config);
  }

  @Bean
  public SubscriberClient subscriberClient() throws IOException, ParseException {
    SubscriberConfig subscriberConfig =
        SubscriberConfig.builder()
            .clientId(subscriberClientId)
            .clientSecret(subscriberClientSecret)
            .privateDecryptionKeys(List.of(loadKey(subscriberPrivateDecryptionKeyPath)))
            .privateSigningKey(loadKey(subscriberPrivateSigningKeyPath))
            .build();

    ApplicationConfig config =
        ApplicationConfig.builder()
            .subscriberConfig(subscriberConfig)
            .activeEnvironment(Environments.TEST.getEnvironmentName())
            .build();

    return ClientFactory.createSubscriberClient(config);
  }

  private JWK loadKey(String filePath) throws IOException, ParseException {
    Resource resource = resourceLoader.getResource(filePath);
    Path path = resource.getFile().toPath();
    String jwkContent = Files.readString(path);

    return JWK.parse(jwkContent);
  }
}
