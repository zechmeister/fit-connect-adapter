package de.bund.digitalservice.a2j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {

  @Generated
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
