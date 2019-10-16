package com.opera.iotapisensors.iotsensor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccelerometerDto extends HighLowValuesContainer {

  @JsonProperty("x")
  private HighLowValuesDto highLowValues_x;

  @JsonProperty("y")
  private HighLowValuesDto highLowValues_y;

  @JsonProperty("z")
  private HighLowValuesDto highLowValues_z;
}
