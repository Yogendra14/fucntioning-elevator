package com.ysingh.elevator.controller;

import static com.ysingh.elevator.config.Mode.MULTIPLE_PASSENGERS;
import static com.ysingh.elevator.config.Mode.SINGLE_PASSENGER;
import static com.ysingh.elevator.config.Optimization.OPTIMAL;
import static com.ysingh.elevator.config.Optimization.SEQUENTIAL;

import com.ysingh.elevator.BaseIT;
import com.ysingh.elevator.ResultMessage;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

class ElevatorControllerIT extends BaseIT {

  private final String apiPath = "/mode/{mode}/{optimization}/executeRequests?input={inputRequest}";
  private final String passengerRequests = "10:8-3;4-6;5-9;5-8";

  @Test
  public void testExecuteRequests_singlePassengerSequentialOptimization() {
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("mode", SINGLE_PASSENGER.getLabel());
    uriVariables.put("optimization", SEQUENTIAL.getLabel());
    uriVariables.put("inputRequest", passengerRequests);
    ResponseEntity<ResultMessage> response = restTemplate.exchange(apiPath, HttpMethod.POST, null, ResultMessage.class, uriVariables);
    Assert.assertEquals("Service Failed.", 200, response.getStatusCodeValue());
    Assert.assertNotNull(response.getBody());
    Assert.assertEquals(22, response.getBody().getTotalFloorsVisited());
  }

  @Test
  public void testExecuteRequests_singlePassengerOptimalOptimization() {
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("mode", SINGLE_PASSENGER.getLabel());
    uriVariables.put("optimization", OPTIMAL.getLabel());
    uriVariables.put("inputRequest", passengerRequests);
    ResponseEntity<ResultMessage> response = restTemplate.exchange(apiPath, HttpMethod.POST, null, ResultMessage.class, uriVariables);
    Assert.assertEquals("Service Failed.", 200, response.getStatusCodeValue());
    Assert.assertNotNull(response.getBody());
    Assert.assertEquals(21, response.getBody().getTotalFloorsVisited());
  }

  @Test
  public void testExecuteRequests_multiplePassengerSequentialOptimization() {
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("mode", MULTIPLE_PASSENGERS.getLabel());
    uriVariables.put("optimization", SEQUENTIAL.getLabel());
    uriVariables.put("inputRequest", passengerRequests);
    ResponseEntity<ResultMessage> response = restTemplate.exchange(apiPath, HttpMethod.POST, null, ResultMessage.class, uriVariables);
    Assert.assertEquals("Service Failed.", 200, response.getStatusCodeValue());
    Assert.assertNotNull(response.getBody());
    Assert.assertEquals(13, response.getBody().getTotalFloorsVisited());
  }

  @Test
  public void testExecuteRequests_multiplePassengerOptimalOptimization() {
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("mode", MULTIPLE_PASSENGERS.getLabel());
    uriVariables.put("optimization", OPTIMAL.getLabel());
    uriVariables.put("inputRequest", passengerRequests);
    ResponseEntity<ResultMessage> response = restTemplate.exchange(apiPath, HttpMethod.POST, null, ResultMessage.class, uriVariables);
    Assert.assertEquals("Service Failed.", 200, response.getStatusCodeValue());
    Assert.assertNotNull(response.getBody());
    Assert.assertEquals(13, response.getBody().getTotalFloorsVisited());
  }

}
