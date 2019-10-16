package com.opera.iotapisensors.iotsensor.dto;

import static java.util.stream.Collectors.groupingBy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.opera.iotapisensors.iotsensor.entities.IotCrackSensorData;

public class CrackdisplacementEntitiesToDtoMapper {

  public CrackdisplacementEntitiesToDtoMapper() {
    columnHeadersWithTypes = new ArrayList<Object>();
    columnHeadersWithTypes.add(new HashMap<String, String>() {
      private static final long serialVersionUID = 6297566572552469283L;

      {
        put("type", "localDateTime");
        put("label", "rcvTimestampUtc");
      }
    });

    columnHeadersWithTypes.add(new HashMap<String, String>() {
      private static final long serialVersionUID = 2265869207482902694L;

      {
        put("type", "number");
        put("label", "crack_value");
      }
    });

    columnHeadersWithMetrics = new ArrayList<Object>();
    columnHeadersWithMetrics.add(new HashMap<String, String>() {
      private static final long serialVersionUID = 6297566572552469288L;

      {
        put("label", "date");
        put("metric", "CET/CEST");

      }
    });

    columnHeadersWithMetrics.add(new HashMap<String, String>() {
      private static final long serialVersionUID = 6297566572552469567L;

      {
        put("metric", "mm");
        put("label", "crack_value");
      }
    });
  }

  private ArrayList<Object> columnHeadersWithMetrics = new ArrayList<Object>();
  private ArrayList<Object> columnHeadersWithTypes = new ArrayList<Object>();

  public List<ProjectIotSensorDto> mapListOfData(String projectId,
      List<IotCrackSensorData> dataList) {

    ArrayList<ProjectIotSensorDto> data = new ArrayList<ProjectIotSensorDto>();

    Map<String, List<IotCrackSensorData>> groupedByMainSection =
        dataList.stream().collect(groupingBy(IotCrackSensorData::getDeviceId));

    for (String deviceId : groupedByMainSection.keySet()) {

      List<IotCrackSensorData> currentMainSectionProgress = groupedByMainSection.get(deviceId);

      ProjectIotSensorDto mainSectionProgress = new ProjectIotSensorDto(projectId);

      mapSectionsAndAdd(mainSectionProgress, currentMainSectionProgress);

      data.add(mainSectionProgress);
    }

    return data;
  }

  private void mapSectionsAndAdd(ProjectIotSensorDto device,
      List<IotCrackSensorData> currentDeviceId) {

    Map<String, List<IotCrackSensorData>> groupedByDeviceId =
        currentDeviceId.stream().collect(groupingBy(IotCrackSensorData::getDeviceId));

    for (String deviceIden : groupedByDeviceId.keySet()) {

      List<IotCrackSensorData> currentSensorData = groupedByDeviceId.get(deviceIden);
      ConstructionSensorDto currentSensor = new ConstructionSensorDto();
      currentSensor.getSensorData().add(columnHeadersWithTypes);

      // assign section metadata from first element
      IotCrackSensorData firstElement = currentSensorData.get(0);
      currentSensor.setDeviceDataMetrics(columnHeadersWithMetrics);

      currentSensor.setDeviceId(firstElement.getDeviceId());
      currentSensor.setDeviceType(firstElement.getDeviceType());

      for (IotCrackSensorData dataPerTimestamp : currentSensorData) {

        ArrayList<Object> data = new ArrayList<Object>();
        data.add(dataPerTimestamp.getMeasuredAt());
        data.add(dataPerTimestamp.getCrack_value());

        currentSensor.getSensorData().add(data);

      }
      device.getData().add(currentSensor);

    }

  }

}
