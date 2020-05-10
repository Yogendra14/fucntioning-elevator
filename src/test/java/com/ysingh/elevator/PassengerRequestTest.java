package com.ysingh.elevator;

import org.junit.Assert;
import org.junit.jupiter.api.Test;


public class PassengerRequestTest {

  @Test
  public void fromStringifyRequest() {
    String passengerRequestString = "10-5";
    PassengerRequest pr = PassengerRequest.fromStringifyRequest(passengerRequestString);
    Assert.assertEquals("Pick up floor doesn't match.", 10, pr.getPickupFloor());
    Assert.assertEquals("Drop off floor doesn't match.", 5, pr.getDropOffFloor());
  }
}
