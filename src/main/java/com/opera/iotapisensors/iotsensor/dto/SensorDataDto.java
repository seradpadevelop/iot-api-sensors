package com.opera.iotapisensors.iotsensor.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataDto {
  private String id;
  private String title;
  private String unitOfMeasurement;
  private int samplingRate;
  private String calibrationValue;
  private Float lastMeasurementValue;
  private Float firstMeasurementValue;
  private List<DataDto> data = new ArrayList<DataDto>();
  
  public void addData(DataDto data) {
    if (data != null) {
      this.data.add(data);
    }
  }
}
