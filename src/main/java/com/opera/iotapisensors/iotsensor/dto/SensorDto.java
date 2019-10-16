package com.opera.iotapisensors.iotsensor.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensorDto {

  private String deviceType;
  private String deviceId;
  private String unitOfMeasurement;
  private String alertLevel;
  private String status;
  private String location;
  private Float latestValue;
  private LocalDateTime dataReceived;

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

  /**
   * @return the unitOfMeasurement
   */
  public String getUnitOfMeasurement() {
    return unitOfMeasurement;
  }

  /**
   * @param unitOfMeasurement the unitOfMeasurement to set
   */
  public void setUnitOfMeasurement(String unitOfMeasurement) {
    this.unitOfMeasurement = unitOfMeasurement;
  }

  /**
   * @return the alertLevel
   */
  public String getAlertLevel() {
    return alertLevel;
  }

  /**
   * @param alertLevel the alertLevel to set
   */
  public void setAlertLevel(String alertLevel) {
    this.alertLevel = alertLevel;
  }

  /**
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * @param status the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * @return the location
   */
  public String getLocation() {
    return location;
  }

  /**
   * @param location the location to set
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * @return the latestValue
   */
  public Float getLatestValue() {
    return latestValue;
  }

  /**
   * @param latestValue the latestValue to set
   */
  public void setLatestValue(Float latestValue) {
    this.latestValue = latestValue;
  }

  /**
   * @return the dataReceived
   */
  public LocalDateTime getDataReceived() {
    return dataReceived;
  }

  /**
   * @param dataReceived the dataReceived to set
   */
  public void setDataReceived(LocalDateTime dataReceived) {
    this.dataReceived = dataReceived;
  }

}
