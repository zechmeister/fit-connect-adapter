package de.bund.digitalservice.a2j.controller;

import de.bund.digitalservice.a2j.service.sender.SenderService;
import de.bund.digitalservice.a2j.service.sender.SubmitRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sender")
public class SenderController {
  private final SenderService service;

  public SenderController(SenderService service) {
    this.service = service;
  }

  @PostMapping("/submit")
  public ResponseEntity<String> submit(@RequestBody SubmitRequest submitRequest) {
    try {
      String response = service.submit(submitRequest);
      return ResponseEntity.ok("Submission successful, content: " + response);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
    }
  }
}
