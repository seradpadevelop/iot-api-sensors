package com.opera.iotapisensors.iotsensor.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * abstract parent class for all types of device graph data used for the minview and maxview
 * 
 */
@Getter
@Setter
public abstract class AbstractSensorDataDto {

  private String timestamp;
  private Float measurement;
}
