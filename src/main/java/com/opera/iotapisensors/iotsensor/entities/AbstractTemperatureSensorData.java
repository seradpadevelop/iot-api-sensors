package com.opera.iotapisensors.iotsensor.entities;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import com.opera.iotapisensors.iotsensor.enums.BaseDataseries;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractTemperatureSensorData  implements DataseriesValues {

  @Column(name = "DEVICEID")
  private String deviceId;
  
  @Column(name = "DEVICETYPE")
  private String deviceType;

  @Column(name = "TEMPERATURE")
  private Float temperature;

  @Column(name = "HUMIDITY")
  private Float humidity;

  @Id
  @Column(name = "MEASUREDAT_UTC")
  private LocalDateTime measuredAt;

  @Column(name = "HISTORIC")
  private Boolean historic;
  
  
  public enum Dataseries implements BaseDataseries {
    HUMIDITY, TEMPERATURE;
  }
  
  @Override
  public <T extends BaseDataseries> Float getDataseriesValue(T ds) {
    if (ds instanceof Dataseries) {
      switch((Dataseries) ds) {
        case HUMIDITY:
          return humidity;
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
