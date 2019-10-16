package com.opera.iotapisensors.iotsensor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrackSensorDataDto extends AbstractSensorDataDto {
  @JsonProperty("id")
  private String id;

  @JsonProperty("timestamp")
  private String timestamp;

  @JsonProperty("crack")
  private Float measurement;

}
