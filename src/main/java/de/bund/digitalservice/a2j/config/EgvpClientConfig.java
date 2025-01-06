package de.bund.digitalservice.a2j.config;

import de.bund.digitalservice.a2j.service.egvp.EgvpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Configuration
public class EgvpClientConfig {

  @Bean
  public EgvpClient egvpClient(@Value("egvp.client.baseUri") String baseUri) {
    RestTemplate restClient =
        new RestTemplateBuilder()
            .errorHandler(new DefaultResponseErrorHandler())
            .rootUri(baseUri)
            .build();

    return new EgvpClient(restClient);
  }
}
