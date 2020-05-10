package com.ysingh.elevator.config;

import java.util.Arrays;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Optimization {
  SEQUENTIAL("sequential"),
  OPTIMAL("optimal")
  ;

  private String label;

  public static Optional<Optimization> from(String type) {
    return Arrays.stream(Optimization.values()).filter(
        label -> label.getLabel().equalsIgnoreCase(type)
    ).findFirst();
  }
}
