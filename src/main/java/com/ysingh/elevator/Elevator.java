package com.ysingh.elevator;

import com.ysingh.elevator.config.Direction;
import com.ysingh.elevator.config.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Component("elevator")
public class Elevator {

  @Builder.Default
  private State state = State.HALTED;

  @Builder.Default
  private Direction movingDirection = Direction.NONE;

  @Builder.Default
  private Integer currentFloor = 1;

  public int move() {
    switch (this.movingDirection) {
      case UP:
        return ++this.currentFloor;
      case DOWN:
        return --this.currentFloor;
      default:
        return 0;
    }
  }

}
