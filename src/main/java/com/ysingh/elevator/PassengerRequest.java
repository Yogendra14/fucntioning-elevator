package com.ysingh.elevator;

import static com.ysingh.elevator.utils.Constants.HYPHEN;

import com.ysingh.elevator.config.Direction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerRequest {

  private int requestNo;

  private int pickupFloor;

  private int dropOffFloor;

  @Builder.Default
  private boolean boarded = false;

  public static PassengerRequest fromStringifyRequest(String request) {
    String[] moveRequest = request.split(HYPHEN);
    return PassengerRequest.builder()
        .pickupFloor(Integer.parseInt(moveRequest[0]))
        .dropOffFloor(Integer.parseInt(moveRequest[1]))
        .boarded(false)
        .build();
  }

  public Direction getRequestedDirection() {
    if (dropOffFloor > pickupFloor) {
      return Direction.UP;
    } else if(dropOffFloor < pickupFloor) {
      return Direction.DOWN;
    } else {
      return Direction.NONE;
    }
  }

  @Override
  public String toString() {
    return this.pickupFloor + "-->" + this.dropOffFloor;
  }
}
