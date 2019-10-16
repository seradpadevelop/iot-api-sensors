package com.opera.iotapisensors.iotsensor.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IotSensorDto {
  private String deviceType;
  private String deviceId;
  private List<SensorDataDto> sensorData;
}
