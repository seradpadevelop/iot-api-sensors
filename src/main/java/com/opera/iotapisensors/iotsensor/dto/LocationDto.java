package com.opera.iotapisensors.iotsensor.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LocationDto {

  private String coordinateSystem;
  private String x;
  private String y;
  private String z;
  private String description;
}
