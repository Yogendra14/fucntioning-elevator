package com.ysingh.elevator.service;

import com.ysingh.elevator.BaseIT;
import com.ysingh.elevator.config.Mode;
import com.ysingh.elevator.config.Optimization;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ElevatorServiceFactoryIT extends BaseIT {

  @Autowired
  ElevatorServiceFactory elevatorServiceFactory;

  @Test
  void testGetElevatorService_singlePassengerSimpleOptimization() {
    ElevatorService elevatorService = elevatorServiceFactory.getElevatorService(Mode.SINGLE_PASSENGER, Optimization.SEQUENTIAL);
    Assert.assertTrue("ElevatorService is not instance of SinglePassengerSimpleService", elevatorService instanceof SinglePassengerSimpleService);
  }

  @Test
  void testGetElevatorService_singlePassengerOptimalOptimization() {
    ElevatorService elevatorService = elevatorServiceFactory.getElevatorService(Mode.SINGLE_PASSENGER, Optimization.OPTIMAL);
    Assert.assertTrue("ElevatorService is not instance of SinglePassengerSimpleService", elevatorService instanceof SinglePassengerOptimalService);
  }

  @Test
  void testGetElevatorService_multiplePassengerSimpleOptimization() {
    ElevatorService elevatorService = elevatorServiceFactory.getElevatorService(Mode.MULTIPLE_PASSENGERS, Optimization.SEQUENTIAL);
    Assert.assertTrue("ElevatorService is not instance of SinglePassengerSimpleService", elevatorService instanceof MultiplePassengerSimpleService);
  }

  @Test
  void testGetElevatorService_multiplePassengerOptimalOptimization() {
    ElevatorService elevatorService = elevatorServiceFactory.getElevatorService(Mode.MULTIPLE_PASSENGERS, Optimization.OPTIMAL);
    Assert.assertTrue("ElevatorService is not instance of SinglePassengerSimpleService", elevatorService instanceof MultiplePassengerOptimalService);
  }

}
