package com.opera.iotapisensors.iotsensor.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AlertLevelDto {
  private Float l1High;
  private Float l2High;
}
