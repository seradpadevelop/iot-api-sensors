package com.opera.iotapisensors.iotsensor.dto;

import java.util.ArrayList;

public class ConstructionSensorDto {

  private String deviceType;
  private String deviceId;
  private ArrayList<Object> deviceDataMetrics = new ArrayList<>();

  private ArrayList<ArrayList<Object>> sensorData = new ArrayList<ArrayList<Object>>();

  public String getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public ArrayList<ArrayList<Object>> getSensorData() {
    return this.sensorData;
  }

  public void setSensorData(ArrayList<ArrayList<Object>> sensorData) {
    this.sensorData = sensorData;
  }

  public ArrayList<Object> getDeviceDataMetrics() {
    return deviceDataMetrics;
  }

  public void setDeviceDataMetrics(ArrayList<Object> deviceDataMetrics) {
    this.deviceDataMetrics = deviceDataMetrics;
  }

}
