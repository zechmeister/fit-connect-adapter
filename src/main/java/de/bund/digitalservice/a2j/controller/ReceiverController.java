package de.bund.digitalservice.a2j.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReceiverController {

  public ReceiverController() {}
  ;

  @PostMapping("callbacks/fit-connect")
  public void newSubmission() {
    System.out.println("new Submission via callback!");
  }
}
