package com.opera.iotapisensors.iotsensor.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "IOT_CRK")
public class IotCrackSensorData extends AbstractCrackSensorData {
  
}
