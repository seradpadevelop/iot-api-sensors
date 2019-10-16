package com.opera.iotapisensors.iotsensor.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@Component
public class DeviceDataMapper {
  private Logger logger = LoggerFactory.getLogger(DeviceDataMapper.class);

  // formatter for defined installation date of "2019-07-09T10:34:54.164"
  String installationPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
  DateTimeFormatter instDateTimeFormatter = DateTimeFormatter.ofPattern(installationPattern);

  // formatter for defined manufacturing date of "1978-12-01"
  String manufacturingPattern = "yyyy-MM-dd";
  DateTimeFormatter mfgDateTimeFormatter = DateTimeFormatter.ofPattern(manufacturingPattern);
  
  
  private void mapProperty(JsonObject srcData, String srcName, JsonObject targetData, String targetName) {
    JsonElement property = srcData.get(srcName);
    if (property != null) {
      targetData.add(targetName, property);
    }
  }

  public DeviceDataDto toDto(String input) throws Exception {
    JsonObject jsonObject = new JsonParser().parse(input).getAsJsonObject();
    DeviceDataDto result = new DeviceDataDto();

    // set basic data
    result.setDeviceType(jsonObject.get("typeId").getAsString());
    result.setDeviceId(jsonObject.get("deviceId").getAsString());

    // set device info
    JsonObject deviceInfo =
        jsonObject.get("deviceInfo") != null ? jsonObject.get("deviceInfo").getAsJsonObject()
            : null;
    if (deviceInfo != null) {
      setDeviceInfoData(deviceInfo, result);
    }

    // set meta data
    JsonObject metadata =
        jsonObject.get("metadata") != null ? jsonObject.get("metadata").getAsJsonObject() : null;
    if (metadata != null) {
      setIdentificationData(metadata, result);
      setInstallationData(metadata, result);
      setLocationData(metadata, result);
      setTechnicalInfoData(metadata, result);
      setDataseries(metadata, result);
      setAlertLevels(metadata, result);
    }

    return result;
  }


  public String updateDeviceData(String oldValues, JsonObject newValues) throws Exception {
    JsonObject result = new JsonParser().parse(oldValues).getAsJsonObject();

    // remove unnecessary basic data
    removeBasicData(result);

    // update device info
    JsonObject deviceInfo =
        result.get("deviceInfo") != null ? result.get("deviceInfo").getAsJsonObject()
            : new JsonObject();
    updateDeviceInfo(newValues, deviceInfo);

    result.add("deviceInfo", deviceInfo);

    // update metadata
    JsonObject metadata = result.get("metadata") != null ? result.get("metadata").getAsJsonObject()
        : new JsonObject();
    updateIdentificationData(newValues, metadata);
    updateInstallationData(newValues, metadata);
    updateLocationData(newValues, metadata);
    updateTechnicalInfoData(newValues, metadata);
    updateDataseries(newValues, metadata);
    updateAlertLevels(newValues, metadata);

    result.add("metadata", metadata);

    return result.toString();
  }


  private void setDeviceInfoData(JsonObject deviceInfo, DeviceDataDto result) {
    JsonElement serialNumber = deviceInfo.get("serialNumber");
    result.setSerialNo(serialNumber != null ? serialNumber.getAsString() : null);
    JsonElement manufacturer = deviceInfo.get("manufacturer");
    result.setManufacturer(manufacturer != null ? manufacturer.getAsString() : null);
    JsonElement model = deviceInfo.get("model");
    result.setModel(model != null ? model.getAsString() : null);
    JsonElement descriptiveLocation = deviceInfo.get("descriptiveLocation");
    result.setLocation(new LocationDto());
    result.getLocation()
        .setDescription(descriptiveLocation != null ? descriptiveLocation.getAsString() : null);
  }

  private void setIdentificationData(JsonObject metadata, DeviceDataDto result) {
    JsonObject identification =
        metadata.get("Identification") != null ? metadata.get("Identification").getAsJsonObject()
            : null;
    if (identification != null) {
      JsonElement projectId = identification.get("ProjectId");
      result.setProjectId(projectId != null ? projectId.getAsString() : null);
      JsonElement channel = identification.get("Channel");
      result.setChannel(channel != null ? channel.getAsString() : null);
      JsonElement transferRate = identification.get("TransferRate");
      result.setTransferRate(transferRate != null ? transferRate.getAsFloat() : null);
    }
  }

  private void setInstallationData(JsonObject metadata, DeviceDataDto result) {
    JsonObject installation =
        metadata.get("Installation") != null ? metadata.get("Installation").getAsJsonObject()
            : null;
    if (installation != null) {
      result.setBreakDownStructure(new ArrayList<String>());
      JsonElement bs1 = installation.get("BreakDownStructure1");
      if (bs1 != null)
        result.getBreakDownStructure().add(bs1.getAsString());
      JsonElement bs2 = installation.get("BreakDownStructure2");
      if (bs2 != null)
        result.getBreakDownStructure().add(bs2.getAsString());
      JsonElement bs3 = installation.get("BreakDownStructure3");
      if (bs3 != null)
        result.getBreakDownStructure().add(bs3.getAsString());
      try {
        JsonElement installationDate = installation.get("InstallationDate");
        result.setInstallationDate(installationDate != null
            ? LocalDateTime.parse(installationDate.getAsString(), instDateTimeFormatter)
            : null);
        JsonElement firstMeasurement = installation.get("FirstMeasurement");
        result.setFirstMeasurement(firstMeasurement != null
            ? LocalDateTime.parse(firstMeasurement.getAsString(), instDateTimeFormatter)
            : null);
        JsonElement lastMeasurement = installation.get("LastMeasurement");
        result.setLastMeasurement(lastMeasurement != null
            ? LocalDateTime.parse(lastMeasurement.getAsString(), instDateTimeFormatter)
            : null);
      } catch (Exception e) {
        logger.error("DeviceDataMapper: Error parsing a installation date to "
            + "DateTime, expected format is " + installationPattern);
      }
    }
  }

  private void setLocationData(JsonObject metadata, DeviceDataDto result) {
    if (result.getLocation() == null)
      result.setLocation(new LocationDto());
    JsonObject coordinates =
        metadata.get("Coordinates") != null ? metadata.get("Coordinates").getAsJsonObject() : null;
    if (coordinates != null) {
      JsonElement coordinateSystem = coordinates.get("CoordinateSystem");
      result.getLocation()
          .setCoordinateSystem(coordinateSystem != null ? coordinateSystem.getAsString() : null);
      JsonElement coordinateX = coordinates.get("CoordinateX");
      result.getLocation().setX(coordinateX != null ? coordinateX.getAsString() : null);
      JsonElement coordinateY = coordinates.get("CoordinateY");
      result.getLocation().setY(coordinateY != null ? coordinateY.getAsString() : null);
      JsonElement coordinateZ = coordinates.get("CoordinateZ");
      result.getLocation().setZ(coordinateZ != null ? coordinateZ.getAsString() : null);
    }
  }

  private void setDataseries(JsonObject metadata, DeviceDataDto result) {
    JsonArray dataseries = metadata.get("Dataseries") != null ? metadata.get("Dataseries").getAsJsonArray() : null;
    if (dataseries != null) {
      List<SensorDataDto> series = new ArrayList<SensorDataDto>();
      
      dataseries.forEach((JsonElement elem) -> {
        SensorDataDto dto = new SensorDataDto();
        JsonObject obj = elem.getAsJsonObject();
        
        JsonElement id = obj.get("Id");
        dto.setId(id != null ? id.getAsString() : null);
        
        JsonElement title = obj.get("Title");
        dto.setTitle(title != null ? title.getAsString() : null);
        
        JsonElement unitOfMeasurement = obj.get("UnitOfMeasurement");
        dto.setUnitOfMeasurement(unitOfMeasurement != null ? unitOfMeasurement.getAsString() : null);
        
        JsonElement samplingRate = obj.get("SamplingRate");
        dto.setSamplingRate(samplingRate != null ? samplingRate.getAsInt() : null);
        
        JsonElement calibrationValue = obj.get("CalibrationValue");
        dto.setCalibrationValue(calibrationValue != null ? calibrationValue.getAsString() : null);
        
        series.add(dto);
      });
      
      result.setDataseries(series);
    }
  }

  private void setTechnicalInfoData(JsonObject metadata, DeviceDataDto result) {
    JsonObject technicalInfo =
        metadata.get("Technical Info") != null ? metadata.get("Technical Info").getAsJsonObject()
            : null;
    if (technicalInfo != null) {
      JsonElement manufacturer = technicalInfo.get("Manufacturer");
      result.setManufacturer(manufacturer != null ? manufacturer.getAsString() : null);
      JsonElement sensorType = technicalInfo.get("SensorType");
      result.setSensorType(sensorType != null ? sensorType.getAsString() : null);
      JsonElement maintainee = technicalInfo.get("Maintainee");
      result.setMaintainee(maintainee != null ? maintainee.getAsString() : null);
      try {
        JsonElement manufacturingDate = technicalInfo.get("ManufacturingDate");
        result.setManufacturingDate(manufacturingDate != null
            ? LocalDate.parse(manufacturingDate.getAsString(), mfgDateTimeFormatter)
            : null);
      } catch (Exception e) {
        logger.error("DeviceDataMapper: Error parsing manufacturing date to "
            + "Date, expected format is " + manufacturingPattern);
      }
      JsonElement ipCode = technicalInfo.get("IPCode");
      result.setIpCode(ipCode != null ? ipCode.getAsString() : null);
      JsonElement shockResistance = technicalInfo.get("ShockResistance");
      result.setShockResistance(shockResistance != null ? shockResistance.getAsString() : null);
      JsonElement measuringRange = technicalInfo.get("Measuring Range");
      result.setMeasuringRange(measuringRange != null ? measuringRange.getAsString() : null);
    }
  }

  private void setAlertLevels(JsonObject metadata, DeviceDataDto result) {
    JsonObject alertLevel =
        metadata.get("AlertLevel") != null ? metadata.get("AlertLevel").getAsJsonObject() : null;
    if (alertLevel != null) {
      result.setAlertLevel(new AlertLevelDto());
      JsonElement l1High = alertLevel.get("L1High");
      result.getAlertLevel().setL1High(l1High != null ? l1High.getAsFloat() : null);
      JsonElement l2High = alertLevel.get("L2High");
      result.getAlertLevel().setL2High(l2High != null ? l2High.getAsFloat() : null);
    }
  }

  private void removeBasicData(JsonObject result) {
    result.remove("clientId");
    result.remove("typeId");
    result.remove("deviceId");
    result.remove("registration");
    result.remove("groups");
    result.remove("status");
    result.remove("gatewayId");
    result.remove("gatewayTypeId");
    result.remove("refs");
  }

  private void updateDeviceInfo(JsonObject update, JsonObject deviceInfo) {
    mapProperty(update, "serialNo", deviceInfo, "serialNumber");
    mapProperty(update, "manufacturer", deviceInfo, "manufacturer");
    mapProperty(update, "model", deviceInfo, "model");
    
    JsonObject location =
        update.get("location") != null ? update.get("location").getAsJsonObject() : null;
    if (location != null) {
      JsonElement description = location.get("description");
      if (description != null)
        deviceInfo.add("descriptiveLocation", description);
    }
  }

  
  private void updateIdentificationData(JsonObject update, JsonObject metadata) {
    JsonObject target =
        metadata.get("Identification") != null ? metadata.get("Identification").getAsJsonObject()
            : new JsonObject();
        
    mapProperty(update, "projectId", target, "ProjectId");
    mapProperty(update, "channel", target, "Channel");
    mapProperty(update, "transferRate", target, "TransferRate");
    
    metadata.add("Identification", target);
  }

  
  private void updateInstallationData(JsonObject update, JsonObject metadata) {
    JsonObject target =
        metadata.get("Installation") != null ? metadata.get("Installation").getAsJsonObject()
            : new JsonObject();
    JsonArray sourceStructure =
        update.get("breakDownStructure") != null ? update.get("breakDownStructure").getAsJsonArray()
            : null;
    if (sourceStructure != null) {
      if (sourceStructure.size() > 0)
        target.add("BreakDownStructure1", sourceStructure.get(0));
      if (sourceStructure.size() > 1)
        target.add("BreakDownStructure2", sourceStructure.get(1));
      if (sourceStructure.size() > 2)
        target.add("BreakDownStructure3", sourceStructure.get(2));
    }
    
    mapProperty(update, "installationDate", target, "InstallationDate");
    mapProperty(update, "firstMeasurement", target, "FirstMeasurement");
    mapProperty(update, "lastMeasurement", target, "LastMeasurement");
    
    metadata.add("Installation", target);
  }

  
  private void updateLocationData(JsonObject update, JsonObject metadata) {
    JsonObject sourceCoordinates =
        update.get("location") != null ? update.get("location").getAsJsonObject() : null;
    if (sourceCoordinates != null) {
      JsonObject target =
          metadata.get("Coordinates") != null ? metadata.get("Coordinates").getAsJsonObject()
              : new JsonObject();
          
      mapProperty(sourceCoordinates, "coordinateSystem", target, "CoordinateSystem");
      mapProperty(sourceCoordinates, "x", target, "CoordinateX");
      mapProperty(sourceCoordinates, "y", target, "CoordinateY");
      mapProperty(sourceCoordinates, "z", target, "CoordinateZ");
      
      metadata.add("Coordinates", target);
    }
  }

  
  // FIXME 2019.09.10, atavio: this should be done properly.. by matching dataseries IDs instead updating them based on their position in the array
  private void updateDataseries(JsonObject update, JsonObject metadata) {
    JsonArray sourceDataseries = update.get("dataseries") != null ? update.get("dataseries").getAsJsonArray() : null;
    if (sourceDataseries != null && sourceDataseries.size() > 0) {
      JsonArray updatedDataseries = new JsonArray();
      for(int i = 0; i < metadata.get("Dataseries").getAsJsonArray().size(); i++) {
        JsonObject sourceDsElem = (i < sourceDataseries.size() && sourceDataseries.get(i) != null) ? sourceDataseries.get(i).getAsJsonObject() : null ;
        JsonObject targetDsElem = metadata.get("Dataseries").getAsJsonArray().get(i).getAsJsonObject();
        
        if (sourceDsElem != null) {
          mapProperty(sourceDsElem, "id", targetDsElem, "Id");
          mapProperty(sourceDsElem, "title", targetDsElem, "Title");
          mapProperty(sourceDsElem, "unitOfMeasurement", targetDsElem, "UnitOfMeasurement");
          mapProperty(sourceDsElem, "samplingRate", targetDsElem, "SamplingRate");
          mapProperty(sourceDsElem, "calibrationValue", targetDsElem, "CalibrationValue");
        }
        
        updatedDataseries.add(targetDsElem);
      }
      
      
      metadata.add("Dataseries", updatedDataseries);
    }
  }
  
  
  private void updateTechnicalInfoData(JsonObject update, JsonObject metadata) {
    JsonObject target =
        metadata.get("Technical Info") != null ? metadata.get("Technical Info").getAsJsonObject()
            : new JsonObject();
        
    mapProperty(update, "manufacturer", target, "Manufacturer");
    mapProperty(update, "sensorType", target, "SensorType");
    mapProperty(update, "maintainee", target, "Maintainee");
    mapProperty(update, "manufacturingDate", target, "ManufacturingDate");
    mapProperty(update, "ipCode", target, "IPCode");
    mapProperty(update, "shockResistance", target, "ShockResistance");
    mapProperty(update, "measuringRange", target, "Measuring Range");
    
    metadata.add("Technical Info", target);
  }

  
  private void updateAlertLevels(JsonObject update, JsonObject metadata) {
    JsonObject alertLevel =
        update.get("alertLevel") != null ? update.get("alertLevel").getAsJsonObject() : null;
    if (alertLevel != null) {
      JsonObject target =
          metadata.get("AlertLevel") != null ? metadata.get("AlertLevel").getAsJsonObject()
              : new JsonObject();
          
      mapProperty(alertLevel, "l1High", target, "L1High");
      mapProperty(alertLevel, "l2High", target, "L2High");
      
      metadata.add("AlertLevel", target);
    }
  }
}
