package com.opera.iotapisensors.iotsensor.enums;

import java.util.stream.Stream;

public enum AlertLevel {
  NORMAL("normal"), WARNING("warning"), CRITICAL("critical");
  
  private final String level;
  
  private AlertLevel(String level) {
    this.level = level;
  }
  
  public static AlertLevel valueOfLevel(String level) {
    return Stream.of(AlertLevel.values()).filter(i -> i.level.equals(level)).findFirst().orElse(null);
  }
  
  @Override
  public String toString() {
    return level;
  }
}
