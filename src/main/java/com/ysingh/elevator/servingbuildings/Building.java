package com.ysingh.elevator.servingbuildings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component("building")
public class Building {

  @Value("${building.floors:10}")
  private int floors;

  @Builder.Default
  @Value("${building.floor.base:1}")
  private int baseFloor=1;

}
