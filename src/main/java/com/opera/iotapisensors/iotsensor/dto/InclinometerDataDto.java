package com.opera.iotapisensors.iotsensor.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InclinometerDataDto extends AbstractSensorDataDto {

  private Integer id;
  private String timestamp;
  private Float rollValue;
  private Float pitchValue;
  private Float mAvgRoll;
  private Float mAvgPitch;

  /**
   * Default constructor
   */
  public InclinometerDataDto(Integer id, String timestamp, Float rollValue,
      Float pitchValue) {
    this.id = id;
    this.timestamp = timestamp;
    this.rollValue = rollValue;
    this.pitchValue = pitchValue;

  }

  public InclinometerDataDto(Integer id, String timestamp, Float rollValue, Float pitchValue,
      Float mAvgRoll, Float mAvgPitch) {
    this.id = id;
    this.timestamp = timestamp;
    this.rollValue = rollValue;
    this.pitchValue = pitchValue;
    this.mAvgRoll = mAvgRoll;
    this.mAvgPitch = mAvgPitch;
  }
}
