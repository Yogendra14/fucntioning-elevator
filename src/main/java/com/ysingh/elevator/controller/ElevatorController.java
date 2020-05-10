package com.ysingh.elevator.controller;

import static com.ysingh.elevator.utils.Constants.COLON;
import static com.ysingh.elevator.utils.Constants.SEMI_COLON;

import com.ysingh.elevator.PassengerRequest;
import com.ysingh.elevator.ResultMessage;
import com.ysingh.elevator.config.Mode;
import com.ysingh.elevator.config.Optimization;
import com.ysingh.elevator.service.ElevatorService;
import com.ysingh.elevator.service.ElevatorServiceFactory;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/")
@RestController
public class ElevatorController {

  @Autowired
  private ElevatorServiceFactory elevatorServiceFactory;

  @PostMapping(value = "/mode/{mode}/{optimization}/executeRequests", produces = "application/json")
  @ResponseBody
  public ResultMessage executeRequests(@PathVariable String mode, @PathVariable String optimization, @RequestParam(name = "input") String input) {
    log.debug("Inside fulfillRequests..");
    Mode runningMode = Mode.from(mode).orElse(Mode.SINGLE_PASSENGER);
    Optimization optimizationMode = Optimization.from(optimization).orElse(Optimization.SEQUENTIAL);
    int startFloor = Integer.parseInt(input.split(COLON)[0]);
    log.debug("startFloor: {}", startFloor);
    String passengerRequests = input.split(COLON)[1];
    log.debug("passengerRequests: {}", passengerRequests);
    ElevatorService elevatorService = elevatorServiceFactory.getElevatorService(runningMode, optimizationMode);
    elevatorService.setStartFloor(startFloor);

      Arrays.stream(passengerRequests.split(SEMI_COLON)).forEach(
          req -> elevatorService.addPassengerRequests(PassengerRequest.fromStringifyRequest(req))
      );
    return elevatorService.move();
  }




}
