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
public abstract class AbstractCrackSensorData implements DataseriesValues {
  
  @Column(name = "DEVICEID")
  private String deviceId;
  
  @Column(name = "DEVICETYPE")
  private String deviceType;
  
  @Column(name = "L")
  private Float crack_value;
  
  @Id
  @Column(name = "MEASUREDAT_UTC")
  private LocalDateTime measuredAt;
  
  @Column(name = "HISTORIC")
  private Boolean historic;

  
  public enum Dataseries implements BaseDataseries {
    LENGTH;
  }
  
  
  @Override
  public <T extends BaseDataseries> Float getDataseriesValue(T ds) {
    if (ds instanceof Dataseries) {
      switch((Dataseries) ds) {
        case LENGTH:
          return crack_value;
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
