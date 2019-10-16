package com.opera.iotapisensors.iotsensor.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.opera.iotapisensors.iotsensor.enums.AlertLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceDataDto {
  private String deviceId;
  private String deviceType;
  private String model;
  private String projectId;
  private String channel;
  private Float transferRate;
  private LocalDateTime installationDate;
  private LocalDateTime firstMeasurement;
  private LocalDateTime lastMeasurement;
  private List<String> breakDownStructure;
  private String manufacturer;
  private String sensorType;
  private String maintainee;
  private LocalDate manufacturingDate;
  private String serialNo;
  private String ipCode;
  private String shockResistance;
  private String measuringRange;
  private LocationDto location;
  private List<SensorDataDto> dataseries;
  private List<SensorDataDto> graph;
  private AlertLevel alertLevelStatus;
  private AlertLevelDto alertLevel;
}
