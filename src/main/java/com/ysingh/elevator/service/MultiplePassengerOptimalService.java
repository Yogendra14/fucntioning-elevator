package com.ysingh.elevator.service;

import com.ysingh.elevator.Elevator;
import com.ysingh.elevator.PassengerRequest;
import com.ysingh.elevator.ResultMessage;
import com.ysingh.elevator.config.Direction;
import com.ysingh.elevator.config.State;
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
public class MultiplePassengerOptimalService implements ElevatorService {

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
    // handling requests sequentially
    while (passengerRequests.size() > 0) {
      log.warn("Requests remaining to handle {}", passengerRequests.size());
      PassengerRequest pr = getNearestPickupOrDropOff();
      log.debug("Nearest {} {}", (pr.isBoarded() ? "Drop off" : "Pick up"), pr.toString());

      while(!pr.isBoarded() && elevator.getCurrentFloor() != pr.getPickupFloor()) {
        setElevatorDirectionForPickup(pr);
        elevator.move();
        log.debug("Move to Floor {}", elevator.getCurrentFloor());
        floorsVisited.add(elevator.getCurrentFloor());
        /*anyPickUps(pr);
        anyDropOffs(pr);*/
      }
      if(!pr.isBoarded()) {
        log.debug("Pick up Passenger {}", pr.getRequestNo());
        //pr.setBoarded(true);
        enRoutePickUps(pr);
        enRouteDropOffs(pr);
        setElevatorDirectionForDropOff(pr);
      }
      while(pr.isBoarded() && elevator.getCurrentFloor() != pr.getDropOffFloor()) {
        setElevatorDirectionForDropOff(pr);
        elevator.move();
        log.debug("Move to Floor {}", elevator.getCurrentFloor());
        floorsVisited.add(elevator.getCurrentFloor());
        enRoutePickUps(pr);
        enRouteDropOffs(pr);
      }
      log.debug("Dropped Off Passenger {}", pr.getRequestNo());
      /*steps.add("Move to Floor " + elevator.getCurrentFloor() + " (Drop off Passenger " + pr.getRequestNo()
        + ") ("
        + getFloorsVisited().size() + ")");*/
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

  private void requestElevatorOrFloor(PassengerRequest pr) {
    if(!pr.isBoarded() && isPassengerOnFloor(pr)) {
      pr.setBoarded(true);
    }
  }

  /*private void anyPickUps(PassengerRequest passengerRequest) {
    //StringBuilder pickUps = new StringBuilder(0);
    String pickUps = passengerRequests.stream()
        //.filter(pr -> pr != passengerRequest)
        .filter(pr -> !pr.isBoarded() && (pr.getPickupFloor() == elevator.getCurrentFloor()) && movingToSameDirection(pr))
        .map(
            pr -> {
              *//*if(pickUps.length() > 0) {
                pickUps.append(" , ").append(pr.getRequestNo());
              }
              pickUps.append(pr.getRequestNo());
              log.debug("anyPickUps: Pick up Passenger {}", pr.getRequestNo());*//*
              pr.setBoarded(true);
              return String.valueOf(pr.getRequestNo());
            }
        ).collect(Collectors.joining(","));
    if(pickUps.length() > 0) {
      log.debug("anyPickUps: Pick up Passenger {}", pickUps);
      steps.add("Move to Floor " + elevator.getCurrentFloor()
          + " (Pick up Passenger " + pickUps
          + ") ("
          + getFloorsVisited().size()
          + ")"
      );
    }
  }

  private void anyDropOffs(PassengerRequest passengerRequest) {
    //StringBuilder droppedOffPassengers = new StringBuilder(0);
    String droppedOffPassengers =
        passengerRequests.stream()
        .filter(pr -> pr != passengerRequest)
        .filter(pr -> pr.isBoarded() && (pr.getDropOffFloor() == elevator.getCurrentFloor()))
        .map(
            pr -> {
              *//*if(droppedOffPassengers.length() > 0) {
                droppedOffPassengers.append(" , ").append(pr.getRequestNo());
              }
              droppedOffPassengers.append(pr.getRequestNo());
              log.debug("anyDropOffs: Drop off Passenger {}", pr.getRequestNo());*//*
              passengerRequests.remove(pr);
              return String.valueOf(pr.getRequestNo());
            }
        ).collect(Collectors.joining(","));
    *//*for (PassengerRequest pr : passengerRequests) {
      if(pr.isBoarded() && pr.getDropOffFloor() == elevator.getCurrentFloor()) {
        if(droppedOffPassengers.length() > 0) {
          droppedOffPassengers.append(" , ").append(pr.getRequestNo());
        }
        droppedOffPassengers.append(pr.getRequestNo());
        passengerRequests.remove(pr);
      }
    }*//*
    if(droppedOffPassengers.length() > 0) {
      log.debug("anyDropOffs: Drop off Passenger {}", droppedOffPassengers);
      steps.add("Move to Floor " + elevator.getCurrentFloor()
          + " (Drop off Passenger " + droppedOffPassengers
          + ") ("
          + getFloorsVisited().size()
          + ")"
      );
    }
  }*/

  private void enRoutePickUps(final PassengerRequest passengerRequest) {
    final StringBuilder pickUps = new StringBuilder(0);
    passengerRequests.stream()
        //.filter(pr -> pr != passengerRequest)
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
      log.debug("anyPickUps: Pick up Passenger {}", pickUps);
      steps.add("Move to Floor " + elevator.getCurrentFloor()
          + " (Pick up Passenger " + pickUps
          + ") ("
          + getFloorsVisited().size()
          + ")"
      );
    }
  }

  private void enRouteDropOffs(final PassengerRequest passengerRequest) {
    String droppedOffPassengers =
        passengerRequests.stream()
            //.filter(pr -> pr != passengerRequest)
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

  private boolean movingToSameDirection(PassengerRequest pr) {
    return pr.getRequestedDirection() == elevator.getMovingDirection();
  }

  private boolean isPassengerOnFloor(PassengerRequest pReq) {
    return pReq.getPickupFloor() == elevator.getCurrentFloor();
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

  private void setDirectionForRequestedFloor(PassengerRequest pReq) {
    if (elevator.getCurrentFloor() < pReq.getDropOffFloor()) {
      elevator.setMovingDirection(Direction.UP);
      elevator.setState(State.MOVING);
    } else if (elevator.getCurrentFloor() > pReq.getDropOffFloor()) {
      elevator.setMovingDirection(Direction.DOWN);
      elevator.setState(State.MOVING);
    } else {
      elevator.setMovingDirection(Direction.NONE);
      elevator.setState(State.HALTED);
    }
  }

  private void boardAndRequestFloor(PassengerRequest pReq) {
    if(pReq.getPickupFloor() == pReq.getDropOffFloor()) {
      log.warn("Passenger {} requested same floor for Pick up & Drop off, skipping Passenger.", pReq.getRequestNo());
      steps.add("Passenger " + pReq.getRequestNo() + " requested same floor for Pick up & Drop off, skipping Passenger.");
      setDirectionForRequestedFloor(pReq);
      passengerRequests.remove(pReq);
    } else {
      log.debug("Pick up Passenger {}", pReq.getRequestNo());
      steps.add("Pick up Passenger " + pReq.getRequestNo());
      pReq.setBoarded(true);
      setDirectionForRequestedFloor(pReq);
    }
    //setDirectionForRequestedFloor(pReq);
  }

  @Override
  public PassengerRequest getNearestPickupOrDropOff() {
    PassengerRequest nearestPassenger = null;
    int shortestDistanceToPickupFloor = building.getFloors();
    for (PassengerRequest pr : passengerRequests) {
      if(isElevatorEmpty()) {
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

  public boolean isElevatorEmpty() {
    PassengerRequest boarded = passengerRequests.stream()
        .filter(PassengerRequest::isBoarded).findAny().orElse(null);
    return boarded == null;
  }

}
