package de.bund.digitalservice.a2j.service.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.UUID;

public class DummyDataGenerator {

  private DummyDataGenerator() {
    throw new IllegalStateException("Utility class");
  }

  public static String generateDummyData(String message) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();

    ObjectNode g17003637 = rootNode.putObject("G17003637");
    g17003637.put("F17005427", UUID.randomUUID().toString().substring(0, 25));
    g17003637.put("F60000319", "Dummy Organisation");
    g17003637.put("F60000228", "John");
    g17003637.put("F60000227", "Doe");

    ObjectNode g60000086 = g17003637.putObject("G60000086");
    g60000086.put("F60000243", "Hauptstraße");
    g60000086.put("F60000244", "123a");
    g60000086.put("F60000246", "12345");
    g60000086.put("F60000247", "Berlin");
    g60000086.put("F60000248", "Zusatzangaben");

    g17003637.put("F60000321", "https://dummy-website.de");

    ObjectNode g60000085 = g17003637.putObject("G60000085");
    g60000085.put("F60000240", "+49-123-4567890");
    g60000085.put("F60000241", "+49-123-4567899");
    g60000085.put("F60000242", "dummy@example.com");
    g60000085.put("F60000345", "dummy@de-mail.de");

    ObjectNode g17006191 = rootNode.putObject("G17006191");
    g17006191.put("F17005648", 2023);
    g17006191.put("F17010371", true);

    ObjectNode g17003640 = mapper.createObjectNode();
    g17003640.put("F17009560", "Nordsee");
    g17003640.put("F17005052", message);
    g17003640.put("F17010371", true);

    ObjectNode g17003643 = g17003640.putArray("G17003643").addObject();
    g17003643.put("F17005652", 8);
    g17003643.put("F17005054", 500);
    g17003643.put("F17005056", 200.5);

    ObjectNode g17003644 = g17003643.putArray("G17003644").addObject();
    g17003644.put("F17005653", "01");
    g17003644.put("F17005654", 10);

    rootNode.putArray("G17003640").add(g17003640);

    return rootNode.toPrettyString();
  }
}
