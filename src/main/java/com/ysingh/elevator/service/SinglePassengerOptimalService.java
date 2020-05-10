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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Scope("prototype")
public class SinglePassengerOptimalService implements ElevatorService {

  @Autowired
  private Building building;
  @Autowired
  private Elevator elevator;
  private int startFloor;
  private final List<PassengerRequest> passengerRequests = new ArrayList<>(0);
  private final List<Integer> floorsVisited = new ArrayList<>(0);
  private final List<String> steps = new ArrayList<>(0);
  AtomicInteger requestCount = new AtomicInteger();

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
  public ResultMessage move() {
    //List<String> steps = new ArrayList<>(0);
    elevator.setCurrentFloor(this.startFloor);
    log.debug("Start at Floor {}", elevator.getCurrentFloor());
    steps.add("Start at Floor " + elevator.getCurrentFloor());
    while (passengerRequests.size() > 0) {
      log.warn("Requests remaining to handle {}", passengerRequests.size());
      // Find nearest Pickup or DropOff
      PassengerRequest pr = getNearestPickupOrDropOff();
      log.debug("Nearest {} : {}", (pr.isBoarded() ? "Drop off" : "Pick up"), pr.toString());

      while(!pr.isBoarded() && elevator.getCurrentFloor() != pr.getPickupFloor()) {
        setElevatorDirectionForPickup(pr);
        elevator.move();
        log.debug("Move to Floor {}", elevator.getCurrentFloor());
        floorsVisited.add(elevator.getCurrentFloor());
      }
      if(!pr.isBoarded()) {
        log.debug("Pick up Passenger {}", pr.getRequestNo());
        steps.add("Move to Floor " + elevator.getCurrentFloor() + " (Pick up Passenger " + pr.getRequestNo()
            + ") ("
            + getFloorsVisited().size() + ")");
        pr.setBoarded(true);
        setElevatorDirectionForDropOff(pr);
      }
      while(pr.isBoarded() && elevator.getCurrentFloor() != pr.getDropOffFloor()) {
        setElevatorDirectionForDropOff(pr);
        elevator.move();
        log.debug("Move to Floor {}", elevator.getCurrentFloor());
        floorsVisited.add(elevator.getCurrentFloor());
      }
      log.debug("Dropped Off Passenger {}", pr.getRequestNo());
      steps.add("Move to Floor " + elevator.getCurrentFloor() + " (Drop off Passenger " + pr.getRequestNo()
          + ") ("
          + getFloorsVisited().size() + ")");
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

  private void handleElevatorDirectionOnEdges() {
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

  public PassengerRequest getNearestPickupOrDropOff() {
    PassengerRequest nearestPassenger = null;
    int shortestDistanceToPickupFloor = building.getFloors();
    for (PassengerRequest pr : passengerRequests) {
      if(!pr.isBoarded()) {
        log.debug("Compare for pickup: {}", pr.toString());
        int distanceToPickupFloor = Math.max(elevator.getCurrentFloor() - pr.getPickupFloor(),
            pr.getPickupFloor() - elevator.getCurrentFloor());
        if (distanceToPickupFloor < shortestDistanceToPickupFloor) {
          shortestDistanceToPickupFloor = distanceToPickupFloor;
          nearestPassenger = pr;
          log.debug("nearest pick up calculated based on pickup floor: {}", pr.toString());
        } else if (distanceToPickupFloor == shortestDistanceToPickupFloor) {
          shortestDistanceToPickupFloor = distanceToPickupFloor;
          nearestPassenger = nearestDropOff(Boolean.FALSE, pr);
          log.debug("nearest pick up calculated based on drop off distance: {}", nearestPassenger.toString());
        }
      }else{
        nearestPassenger = nearestDropOff(Boolean.TRUE, pr);
      }
    }
    return nearestPassenger;
  }

  private PassengerRequest nearestDropOff(Boolean isBoarded, PassengerRequest passengerRequest) {
    PassengerRequest nearestDrop = null;
    int shortestDropOff = building.getFloors();
    for (PassengerRequest pr : passengerRequests) {
      if(isBoarded.equals(pr.isBoarded()) && pr.getPickupFloor() == passengerRequest.getPickupFloor()) {
        log.debug("Calculate nearest Drop Off {}", pr.toString());
        int distanceToDropOffFloor = Math.max(pr.getDropOffFloor() - pr.getPickupFloor(),
            pr.getPickupFloor() - pr.getDropOffFloor());
        if (distanceToDropOffFloor < shortestDropOff) {
          shortestDropOff = distanceToDropOffFloor;
          nearestDrop = pr;
        }
      }
    }
    return nearestDrop;
  }

}
