package com.opera.iotapisensors.iotsensor.entities;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "CH_TEMPERATURE")
@AttributeOverride(name = "measuredAt", column = @Column(name="MEASUREDAT"))
public class ChTemperatureData extends AbstractTemperatureSensorData {

}
