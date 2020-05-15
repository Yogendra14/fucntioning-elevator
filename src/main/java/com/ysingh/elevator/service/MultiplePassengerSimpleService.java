package com.ysingh.elevator.service;

import com.ysingh.elevator.Elevator;
import com.ysingh.elevator.PassengerRequest;
import com.ysingh.elevator.ResultMessage;
import com.ysingh.elevator.config.Direction;
import com.ysingh.elevator.servingbuildings.Building;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Scope("prototype")
public class MultiplePassengerSimpleService implements ElevatorService {

  @Autowired
  private Building building;
  @Autowired
  private Elevator elevator;

  private int startFloor;

  AtomicInteger requestCount = new AtomicInteger();
  private final List<PassengerRequest> passengerRequests = new ArrayList<>(0);

  private final List<Integer> floorsVisited = new ArrayList<>(0);
  private final List<String> steps = new ArrayList<>(0);

  @Override
  public List<Integer> getFloorsVisited() {
    return Collections.unmodifiableList(floorsVisited);
  }

  @Override
  public void setStartFloor(int floor) {
    this.startFloor = floor;
  }

  @Override
  public void addPassengerRequests(PassengerRequest passengerRequest) {
    passengerRequest.setRequestNo(requestCount.incrementAndGet());
    this.passengerRequests.add(passengerRequest);
  }

  @Override
  public PassengerRequest getNearestPickupOrDropOff() {
    return passengerRequests.get(0);
  }

  @Override
  public ResultMessage move() {
    elevator.setCurrentFloor(this.startFloor);
    log.debug("Start at Floor {}", elevator.getCurrentFloor());
    steps.add("Start at Floor " + elevator.getCurrentFloor());
    // handling requests sequentially
    while (passengerRequests.size() > 0) {
      log.warn("Requests remaining to handle {}", passengerRequests.size());
      PassengerRequest pr = getNearestPickupOrDropOff();

      log.debug("Current {}: {}", (pr.isBoarded() ? "Drop off" : "Pick up"), pr.toString());

      while(!pr.isBoarded() && elevator.getCurrentFloor() != pr.getPickupFloor()) {
        setElevatorDirectionForPickup(pr);
        elevator.move();
        log.debug("Move to Floor {}", elevator.getCurrentFloor());
        floorsVisited.add(elevator.getCurrentFloor());
      }
      if(!pr.isBoarded() && elevator.getCurrentFloor() == pr.getPickupFloor()) {
        log.debug("Pick up Passenger {}", pr.getRequestNo());
        steps.add("Move to Floor " + elevator.getCurrentFloor() + " (Pickup Passenger " + pr.getRequestNo()
              + ") ("
              + getFloorsVisited().size() + ")");
        pr.setBoarded(true);
        setElevatorDirectionForDropOff(pr);
        enRoutePickups();
        enRouteDropOffs();
      }
      while(pr.isBoarded() && elevator.getCurrentFloor() != pr.getDropOffFloor()) {
        setElevatorDirectionForDropOff(pr);
        elevator.move();
        log.debug("Move to Floor {}", elevator.getCurrentFloor());
        floorsVisited.add(elevator.getCurrentFloor());
        enRoutePickups();
        enRouteDropOffs();
      }
      passengerRequests.remove(pr);
    }
    log.debug("Total {} Floor visited: {}", floorsVisited.size(), floorsVisited);
    return ResultMessage.builder()
        .steps(steps)
        .floorsVisited(getFloorsVisited()).totalFloorsVisited(getFloorsVisited().size()).build();
  }

  private void setElevatorDirectionForDropOff(PassengerRequest pr) {
    if(elevator.getCurrentFloor() > pr.getDropOffFloor()){
      elevator.setMovingDirection(Direction.DOWN);
    }else if(elevator.getCurrentFloor() < pr.getDropOffFloor()){
      elevator.setMovingDirection(Direction.UP);
    }else {
      elevator.setMovingDirection(Direction.NONE);
    }
    handleElevatorDirectionOnEdges();
  }

  private void setElevatorDirectionForPickup(PassengerRequest pr) {
    if(elevator.getCurrentFloor() > pr.getPickupFloor()){
      elevator.setMovingDirection(Direction.DOWN);
    }else if(elevator.getCurrentFloor() < pr.getPickupFloor()){
      elevator.setMovingDirection(Direction.UP);
    }else {
      elevator.setMovingDirection(Direction.NONE);
    }
    handleElevatorDirectionOnEdges();
  }

  protected void enRoutePickups() {
    final StringBuilder pickUps = new StringBuilder(0);
    passengerRequests.stream()
        .filter(pr -> !pr.isBoarded() && (pr.getPickupFloor() == elevator.getCurrentFloor()) && movingToSameDirection(pr))
        .forEachOrdered(
            pr -> {
              if(pickUps.length() > 0) {
                pickUps.append(",");
              }
              pickUps.append(pr.getRequestNo());
              pr.setBoarded(true);
            }
        );
    if(pickUps.length() > 0) {
      log.debug("enRoutePickups: Pick up Passenger {}", pickUps);
      steps.add("Move to Floor " + elevator.getCurrentFloor()
          + " (Pick up Passenger " + pickUps
          + ") ("
          + getFloorsVisited().size()
          + ")"
      );
    }
  }

  protected void enRouteDropOffs() {
    String droppedOffPassengers =
        passengerRequests.stream()
        .filter(pr -> (pr.isBoarded() && (pr.getDropOffFloor() == elevator.getCurrentFloor())))
        .map(pr -> String.valueOf(pr.getRequestNo())
        ).collect(Collectors.joining(","));

    passengerRequests.removeIf(pr -> (pr.isBoarded() && (pr.getDropOffFloor() == elevator.getCurrentFloor())));
    if(droppedOffPassengers.length() > 0) {
      log.debug("enRouteDropOffs: Drop off Passenger {}", droppedOffPassengers);
      steps.add("Move to Floor " + elevator.getCurrentFloor()
          + " (Drop off Passenger " + droppedOffPassengers
          + ") ("
          + getFloorsVisited().size()
          + ")"
      );
    }
  }

  protected boolean movingToSameDirection(PassengerRequest pr) {
    return pr.getRequestedDirection() == elevator.getMovingDirection();
  }

  protected void handleElevatorDirectionOnEdges() {
    // On Base Floor
    if (elevator.getCurrentFloor() == building.getBaseFloor()) {
      log.debug("On Base floor. Can only move UP");
      elevator.setMovingDirection(Direction.UP);
    }
    // On Top Floor
    if (elevator.getCurrentFloor() == building.getFloors()) {
      log.debug("On Top floor. Can only move DOWN");
      elevator.setMovingDirection(Direction.DOWN);
    }
  }

}
