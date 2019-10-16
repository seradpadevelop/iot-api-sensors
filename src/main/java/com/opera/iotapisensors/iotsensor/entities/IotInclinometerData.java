package com.opera.iotapisensors.iotsensor.entities;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.opera.iotapisensors.iotsensor.enums.BaseDataseries;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "IOT_INKLINOMETER")
public class IotInclinometerData implements DataseriesValues {

  @Column(name = "DEVICEID")
  private String deviceId;

  @Column(name = "MEASUREDAT_UTC")
  private LocalDateTime measuredAt;

  @Column(name = "ROLL_VALUE")
  private Float rollValue;

  @Column(name = "PITCH_VALUE")
  private Float pitchValue;

  @Column(name = "ROLL_MAVG")
  private Float rollMavg;

  @Column(name = "PITCH_MAVG")
  private Float pitchMavg;

  @Column(name = "DEVICETYPE")
  private String deviceType;

  @Column(name = "LOGICALINTERFACE_ID")
  private String logicalInterfaceId;

  @Column(name = "EVENTTYPE")
  private String eventType;

  @Column(name = "FORMAT")
  private String format;

  @Id
  @Column(name = "RCV_TIMESTAMP_UTC")
  private LocalDateTime rcvTimestampUtc;
  
  
  public enum Dataseries implements BaseDataseries {
    ROLL, PITCH, ROLL_M_AVG, PITCH_M_AVG;
  }
  
  
  @Override
  public <T extends BaseDataseries> Float getDataseriesValue(T ds) {
    if (ds instanceof Dataseries) {
      switch((Dataseries) ds) {
        case ROLL:
          return rollValue;
        case PITCH:
          return pitchValue;
        case ROLL_M_AVG:
          return rollMavg;
        case PITCH_M_AVG:
          return pitchMavg;
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
