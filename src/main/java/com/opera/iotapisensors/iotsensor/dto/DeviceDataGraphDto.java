package com.opera.iotapisensors.iotsensor.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceDataGraphDto {

  private String deviceType;
  private String deviceId;
  private List<SensorDataDto> sensorData;
  private List<SensorDataDto> graph;

}
