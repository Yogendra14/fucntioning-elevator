package com.ysingh.elevator.config;

import java.util.Arrays;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Mode {
  SINGLE_PASSENGER("single"),
  MULTIPLE_PASSENGERS("multiple")
  ;

  private String label;

  public static Optional<Mode> from(String type) {
    return Arrays.stream(Mode.values()).filter(
        label -> label.getLabel().equalsIgnoreCase(type)
    ).findFirst();
  }

}
