package de.bund.digitalservice.a2j.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sender")
public class SenderController {
  public SenderController() {}

  @PostMapping("/submit")
  public ResponseEntity<String> submitMessage(@RequestBody String message) {
    return ResponseEntity.ok("Received message: " + message);
  }
}
