package de.bund.digitalservice.a2j.config;

import dev.fitko.fitconnect.api.config.ApplicationConfig;
import dev.fitko.fitconnect.client.bootstrap.ApplicationConfigLoader;
import java.io.IOException;
import java.nio.file.Path;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class FitConnectConfig {
  private final ResourceLoader resourceLoader;

  public FitConnectConfig(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  @Bean
  public ApplicationConfig applicationConfig() throws IOException {
    return ApplicationConfigLoader.loadConfigFromPath(
        Path.of(resourceLoader.getResource("classpath:fitConnectConfig.yaml").getURI()));
  }
}
