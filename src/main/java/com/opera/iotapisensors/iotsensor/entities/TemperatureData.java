package com.opera.iotapisensors.iotsensor.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "IOT_TEMPERATURE")
public class TemperatureData extends AbstractTemperatureSensorData {

}
