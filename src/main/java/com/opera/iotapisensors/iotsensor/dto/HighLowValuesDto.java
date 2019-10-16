package com.opera.iotapisensors.iotsensor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HighLowValuesDto {

  private Float lowValue;
  private Float highValue;
  private String lowTimestamp;
  private String highTimestamp;
}
