package com.opera.iotapisensors.iotsensor.entities;

import java.time.LocalDateTime;
import com.opera.iotapisensors.iotsensor.enums.BaseDataseries;

public interface DataseriesValues {
  public <T extends BaseDataseries> T getDataseriesByName(String name);
  public <T extends BaseDataseries> Float getDataseriesValue(T ds);
  public LocalDateTime getMeasuredAt();
}
