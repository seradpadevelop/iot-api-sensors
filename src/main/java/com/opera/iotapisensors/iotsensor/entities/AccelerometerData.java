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
@Table(name = "IOT_ACCELEROMETER")
public class AccelerometerData implements DataseriesValues {

  @Column(name = "VALUE1")
  private Float value1;

  @Column(name = "MEASUREDAT_UTC")
  private LocalDateTime measuredAt;

  @Column(name = "DEVICETYPE")
  private String deviceType;

  @Column(name = "DEVICEID")
  private String deviceId;

  @Column(name = "LOGICALINTERFACE_ID")
  private String logicalInterfaceId;

  @Column(name = "EVENTTYPE")
  private String eventType;

  @Column(name = "FORMAT")
  private String format;

  @Id
  @Column(name = "RCV_TIMESTAMP_UTC")
  private LocalDateTime rcvTimestampUtc;

  @Column(name = "UPDATED_UTC")
  private LocalDateTime updatedUtc;
  
  public enum Dataseries implements BaseDataseries {
    ACCELERATION;
  }
  
  @Override
  public <T extends BaseDataseries> Float getDataseriesValue(T ds) {
    if (ds instanceof Dataseries) {
      switch((Dataseries) ds) {
        case ACCELERATION:
          return value1;
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
