package com.opera.iotapisensors.iotsensor.dto;

import java.util.ArrayList;
import java.util.List;

public class ProjectIotSensorDto {

  private String projectId;

  private List<ConstructionSensorDto> Data = new ArrayList<ConstructionSensorDto>();

  public ProjectIotSensorDto(String projectId) {
    super();
    this.projectId = projectId;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public List<ConstructionSensorDto> getData() {
    return Data;
  }

  public void setData(List<ConstructionSensorDto> data) {
    Data = data;
  }

  
}
