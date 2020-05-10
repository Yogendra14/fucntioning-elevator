package com.ysingh.elevator.service;

import com.ysingh.elevator.Elevator;
import com.ysingh.elevator.PassengerRequest;
import com.ysingh.elevator.ResultMessage;
import com.ysingh.elevator.config.Direction;
import com.ysingh.elevator.config.State;
import com.ysingh.elevator.servingbuildings.Building;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Scope("prototype")
public class SinglePassengerSimpleService implements ElevatorService {

  @Autowired
  private Building building;
  @Autowired
  private Elevator elevator;

  private int startFloor;
  private final Queue<PassengerRequest> passengerRequests = new LinkedList<>();
  private final List<Integer> floorsVisited = new ArrayList<>(0);
  private final List<String> steps = new ArrayList<>(0);
  AtomicInteger requestCount = new AtomicInteger();

  @Override
  public List<Integer> getFloorsVisited() {
    return Collections.unmodifiableList(floorsVisited);
  }

  @Override
  public PassengerRequest getNearestPickupOrDropOff() {
    return this.passengerRequests.peek();
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
    elevator.setCurrentFloor(this.startFloor);
    log.debug("Start at Floor: {}", elevator.getCurrentFloor());
    steps.add("Start at Floor: " + elevator.getCurrentFloor());
    while (passengerRequests.peek() != null) {
      PassengerRequest pReq = passengerRequests.poll();
      elevator.setState(State.MOVING);
      while (true) {
        // While moving in Elevator
        if (pReq.isBoarded()) {
          requestFloor(pReq);
          if(reachedDropOffFloor(pReq)){
            log.debug("Drop Off passenger {}", pReq.getRequestNo());
            steps.add("Move to Floor " + elevator.getCurrentFloor() + " (Drop off Passenger " + pReq.getRequestNo() + ") (" + getFloorsVisited().size() + ")");
            dropOff(pReq);
            break;
          }
        } else { // while still waiting for Elevator
          requestElevator(pReq);
          if (pReq.getPickupFloor() == elevator.getCurrentFloor()) {
            log.debug("Pick up Passenger {}", pReq.getRequestNo());
            steps.add("Move to Floor " + elevator.getCurrentFloor() + " (Pick up Passenger " + pReq.getRequestNo() + ") (" + getFloorsVisited().size() + ")");
            boardElevator(pReq);
          }
        }
        handleEdgeConditions();
        elevator.move();
        log.debug("Move to Floor {}", elevator.getCurrentFloor());
        floorsVisited.add(elevator.getCurrentFloor());
      }
    }
    log.debug("Total {} Floor visited: {}", floorsVisited.size(), floorsVisited);
    return ResultMessage.builder()
        .steps(steps)
        .floorsVisited(getFloorsVisited())
        .totalFloorsVisited(getFloorsVisited().size())
        .build();
  }

  private boolean reachedDropOffFloor(PassengerRequest pReq) {
    return elevator.getCurrentFloor() == pReq.getDropOffFloor();
  }

  private void handleEdgeConditions() {
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

  private void dropOff(PassengerRequest pReq) {
    elevator.setState(State.HALTED);
    elevator.setMovingDirection(Direction.NONE);
    passengerRequests.remove(pReq);
  }

  private void requestFloor(PassengerRequest pReq) {
    if (elevator.getCurrentFloor() < pReq.getDropOffFloor()) {
      elevator.setMovingDirection(Direction.UP);
      elevator.setState(State.MOVING);
    } else if (elevator.getCurrentFloor() > pReq.getDropOffFloor()) {
      elevator.setMovingDirection(Direction.DOWN);
      elevator.setState(State.MOVING);
    }
  }

  private void boardElevator(PassengerRequest pReq) {
    pReq.setBoarded(true);
    requestFloor(pReq);
  }

  private void requestElevator(PassengerRequest pReq) {
    if (pReq.getPickupFloor() > elevator.getCurrentFloor()) {
      elevator.setMovingDirection(Direction.UP);
    } else if (pReq.getPickupFloor() < elevator.getCurrentFloor()) {
      elevator.setMovingDirection(Direction.DOWN);
    }
  }

}
