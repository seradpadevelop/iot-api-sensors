package com.opera.iotapisensors.iotsensor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrackSensorDto extends HighLowValuesContainer {

  @JsonProperty("riss")
  private HighLowValuesDto highLowValues_crack;

}
