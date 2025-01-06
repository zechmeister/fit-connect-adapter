package de.bund.digitalservice.a2j.repository.egvp;

public record MessageInTransit(String userId, String customId, String bundIdMailbox) {

  public int hashCode() {

    return customId.hashCode();
  }

  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (obj.getClass() != this.getClass()) {
      return false;
    }

    return customId.equals(((MessageInTransit) obj).customId)
        && userId.equals(((MessageInTransit) obj).userId);
  }
}
