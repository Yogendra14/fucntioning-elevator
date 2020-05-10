package com.ysingh.elevator.service;

import com.ysingh.elevator.PassengerRequest;
import com.ysingh.elevator.ResultMessage;
import java.util.List;

public interface ElevatorService {

  List<Integer> getFloorsVisited();

  void setStartFloor(int floor);

  void addPassengerRequests(PassengerRequest passengerRequest);

  PassengerRequest getNearestPickupOrDropOff();

  ResultMessage move();
}
