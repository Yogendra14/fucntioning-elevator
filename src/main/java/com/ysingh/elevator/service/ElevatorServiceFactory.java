package com.ysingh.elevator.service;

import com.ysingh.elevator.config.Mode;
import com.ysingh.elevator.config.Optimization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ElevatorServiceFactory {

  @Lookup
  public SinglePassengerSimpleService singlePassengerSimpleService() { return null;}
  @Lookup
  public SinglePassengerOptimalService singlePassengerOptimalService() {return null;}
  @Lookup
  public MultiplePassengerSimpleService multiplePassengerSimpleService() { return null;}
  @Lookup
  public MultiplePassengerOptimalService multiplePassengerOptimalService() {return null;}

  public ElevatorService getElevatorService(Mode runningMode, Optimization optimizationMode) {
    switch (optimizationMode) {
      case SEQUENTIAL:
        if (runningMode == Mode.SINGLE_PASSENGER) {
          return singlePassengerSimpleService();
        }
        return multiplePassengerSimpleService();
      case OPTIMAL:
        if (runningMode == Mode.SINGLE_PASSENGER) {
          return singlePassengerOptimalService();
        }
        return multiplePassengerOptimalService();
      default:
        log.warn("Invalid Mode/OptimizationMode Provided, falling back to SinglePassengerSimpleService mode");
        return singlePassengerSimpleService();
    }
  }

}
