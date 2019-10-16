package com.opera.iotapisensors.iotsensor.entities;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.opera.iotapisensors.iotsensor.enums.BaseDataseries;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "IOT_IMU")
public class ImuData implements DataseriesValues {

  @Column(name = "MEASUREDAT_UTC")
  private LocalDateTime measuredAt;

  @Column(name = "PITCH")
  private Float pitch;

  @Column(name = "Roll")
  private Float roll;

  @Column(name = "YAW")
  private Float yaw;

  @Column(name = "ACCELERATION_X")
  private Float accelerationX;

  @Column(name = "ACCELERATION_Y")
  private Float accelerationY;

  @Column(name = "ACCELERATION_Z")
  private Float accelerationZ;

  @Column(name = "TEMPERATURE")
  private Float temperature;

  @Column(name = "DEVICETYPE")
  private String deviceType;

  @Column(name = "DEVICEID")
  private String deviceId;

  @Column(name = "HISTORIC")
  private Boolean historic;

  @Id
  @Column(name = "RCV_TIMESTAMP_UTC")
  private LocalDateTime rcvTimestampUtc;
  
  
  public enum Dataseries implements BaseDataseries {
    ROLL, PITCH, YAW, ACCELERATION_X, ACCELERATION_Y, ACCELERATION_Z, TEMPERATURE;
  }


  @Override
  public <T extends BaseDataseries> Float getDataseriesValue(T ds) {
    if (ds instanceof Dataseries) {
      switch((Dataseries) ds) {
        case ROLL:
          return roll;
        case PITCH:
          return pitch;
        case YAW:
          return yaw;
        case ACCELERATION_X:
          return accelerationX;
        case ACCELERATION_Y:
          return accelerationY;
        case ACCELERATION_Z:
          return accelerationZ;
        case TEMPERATURE:
          return temperature;
        default:
          throw new IllegalArgumentException("Unknown dataseries: " + ds.name());
      }
    } else {
      throw new IllegalArgumentException("Unknown dataseries: " + ds.name());
    }
  }
  
  @SuppressWarnings("unchecked")
  public Dataseries getDataseriesByName(String name) {
    return Dataseries.valueOf(name);
  }
}
