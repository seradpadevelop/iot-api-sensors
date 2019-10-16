package com.opera.iotapisensors.iotsensor.enums;

import java.util.stream.Stream;

public enum DeviceType {
  ACCELEROMETER("accelerometer"), INCLINOMETER("Inklinometer"), CRACK("Crk"), IMU("IMU"), TEMPERATURE("Temperature");
  
  private final String label;
  
  private DeviceType(String label) {
    this.label = label;
  }
  
  public static DeviceType valueOfLabel(String label) {
    return Stream.of(DeviceType.values()).filter(i -> i.label.equals(label)).findFirst().orElse(null);
  }
  
  public String getLabel() {
    return this.label;
  }
  
  @Override
  public String toString() {
    return getLabel();
  }

}
