package com.opera.iotapisensors.iotsensor.dto;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@RunWith(JUnit4.class)
public class DeviceDataMapperTest {
  private DeviceDataMapper underTest = new DeviceDataMapper();
  
  
  private JsonObject toJsonObject(String str) {
    return new JsonParser().parse(str).getAsJsonObject();
  }
  
  @Test
    public void testToDto_WithDataseriesAsArray() throws Exception {
      // Given:
      int expectedDataseriesSize = 4;
      String expectedTitle = "Roll";
      String expectedId = "ROLL";
      String expectedUnitOfMeasurement = "m/s^2";
      int expectedSamplingRate = 100;
      String expectedCalibrationValue = "6 mm";
      SensorDataDto expectedFirstSensorDataDto = new SensorDataDto(
          expectedId,
          expectedTitle,
          expectedUnitOfMeasurement,
          expectedSamplingRate,
          expectedCalibrationValue,
          null,
          null,
          new ArrayList<DataDto>()
          );
      // @formatter:off
      String expectedDeviceConfiguration = String.format("{\n" + 
          "    \"clientId\": \"d:jbba0g:Inklinometer:Inklinometer01\",\n" + 
          "    \"typeId\": \"Inklinometer\",\n" + 
          "    \"deviceId\": \"Inklinometer01\",\n" + 
          "    \"metadata\": {\n" + 
          "        \"Dataseries\": [\n" + 
          "            {\n" + 
          "                \"Id\": \"%s\",\n" + 
          "                \"Title\": \"%s\",\n" +
          "                \"UnitOfMeasurement\": \"%s\",\n" + 
          "                \"SamplingRate\": %d,\n" + 
          "                \"CalibrationValue\": \"%s\"\n" + 
          "            },\n" + 
          "            {\n" +
          "                \"Id\": \"PITCH\",\n" +
          "                \"Title\": \"Pitch\",\n" + 
          "                \"UnitOfMeasurement\": \"m/s^2\",\n" + 
          "                \"SamplingRate\": 100,\n" + 
          "                \"CalibrationValue\": \"6 mm\"\n" + 
          "            },\n" + 
          "            {\n" +
          "                \"Id\": \"ROLL_M_AVG\",\n" +
          "                \"Title\": \"Roll Moving Average\",\n" + 
          "                \"UnitOfMeasurement\": \"m/s^2\",\n" + 
          "                \"SamplingRate\": 100,\n" + 
          "                \"CalibrationValue\": \"6 mm\"\n" + 
          "            },\n" + 
          "            {\n" + 
          "                \"Id\": \"PITCH_M_AVG\",\n" + 
          "                \"Title\": \"Pitch Moving Average\",\n" +
          "                \"UnitOfMeasurement\": \"m/s^2\",\n" + 
          "                \"SamplingRate\": 100,\n" + 
          "                \"CalibrationValue\": \"6 mm\"\n" + 
          "            }\n" + 
          "        ],\n" + 
          "        \"AlertLevel\": {\n" + 
          "            \"L1High\": 0.0147138,\n" + 
          "            \"L2High\": \"11\"\n" + 
          "        }\n" + 
          "    }\n" + 
          "}", expectedId, expectedTitle, expectedUnitOfMeasurement, expectedSamplingRate, expectedCalibrationValue);
      // @formatter:on
  
      // When:
      DeviceDataDto actualDeviceData = underTest.toDto(expectedDeviceConfiguration);
  
      // Then:
      assertThat(actualDeviceData.getDataseries()).hasSize(expectedDataseriesSize);
      assertThat(actualDeviceData.getDataseries()).first().isEqualTo(expectedFirstSensorDataDto);
    }
  
  
  @Test
    public void testUpdateDeviceData_updatesBreakdownAndUnitsOfMeasurement() throws Exception {
      // Given:
      String expectedDataseriesId = "ACCELERATION";
      String expectedOldValues = String.format("{" + 
          "    \"clientId\": \"d:jbba0g:accelerometer:accelerometerffm\"," + 
          "    \"typeId\": \"accelerometer\"," + 
          "    \"deviceId\": \"accelerometerffm\"," + 
          "    \"deviceInfo\": {}," + 
          "    \"metadata\": {" + 
          "        \"Installation\": {" + 
          "            \"InstallationDate\": \"2019-07-09T10:34:54.164\"," + 
          "            \"FirstMeasurement\": \"2019-08-09T11:44:50.832\"," + 
          "            \"BreakDownStructure1\": \"30A1\"," + 
          "            \"BreakDownStructure2\": \"BA03\"," + 
          "            \"BreakDownStructure3\": \"45A2\"" + 
          "        }," + 
          "        \"Technical Info\": {" + 
          "            \"Maintainee\": \"Max M.\"," + 
          "            \"ManufacturingDate\": \"1978-12-01\"," + 
          "            \"IPCode\": \"IP68\"," + 
          "            \"Manufacturer\": \"Siemens\"," + 
          "            \"SensorType\": \"R2-D2\"," + 
          "            \"ShockResistance\": \"1128\"," + 
          "            \"Measuring Range\": \"3-48\"" + 
          "        }," + 
          "        \"Coordinates\": {" + 
          "            \"CoordinateSystem\": \"GK 3 [EPSG:42500]\"," + 
          "            \"CoordinateX\": \"3256578.2542\"," + 
          "            \"CoordinateY\": \"525365.1252\"," + 
          "            \"CoordinateZ\": \"1010.2542\"" + 
          "        }," + 
          "        \"Dataseries\": [" + 
          "            {" + 
          "                \"Id\": \"%s\"," + 
          "                \"Title\": \"Acceleration\"," + 
          "                \"UnitOfMeasurement\": \"mm/s\"," + 
          "                \"SamplingRate\": 100," + 
          "                \"CalibrationValue\": \"7 mm/s\"" + 
          "            }" + 
          "        ]," + 
          "        \"Identification\": {" + 
          "            \"DeviceId\": \"accelerometerffm\"," + 
          "            \"ProjectId\": \"1\"," + 
          "            \"Channel\": \"30T5\"," + 
          "            \"TransferRate\": 50" + 
          "        }," + 
          "        \"AlertLevel\": {" + 
          "            \"L1High\": \"2\"," + 
          "            \"L2High\": \"3\"" + 
          "        }" + 
          "    }," + 
          "    \"registration\": {" + 
          "        \"auth\": {" + 
          "            \"id\": \"g:jbba0g:Sensorbox:Sensorbox02\"," + 
          "            \"type\": \"dev\"" + 
          "        }," + 
          "        \"date\": \"2019-07-02T13:06:58.922Z\"" + 
          "    }," + 
          "    \"groups\": [" + 
          "        \"gw_def_res_grp:jbba0g:Sensorbox:Sensorbox02\"" + 
          "    ]," + 
          "    \"gatewayId\": \"Sensorbox02\"," + 
          "    \"gatewayTypeId\": \"Sensorbox\"," + 
          "    \"refs\": {" + 
          "        \"diag\": {" + 
          "            \"logs\": \"/api/v0002/device/types/accelerometer/devices/accelerometerffm/diag/logs\"," + 
          "            \"errorCodes\": \"/api/v0002/device/types/accelerometer/devices/accelerometerffm/diag/errorCodes\"" + 
          "        }," + 
          "        \"location\": \"/api/v0002/device/types/accelerometer/devices/accelerometerffm/location\"," + 
          "        \"clientState\": \"/api/v0002/clientconnectionstates/d:jbba0g:accelerometer:accelerometerffm\"" + 
          "    }" + 
          "}", expectedDataseriesId);
      String expectedBreakdownStructure = "30A11";
      String expectedUnitOfMeasurement = "m";
      String updateData = String.format("{" + 
          "    \"channel\": \"30T5\"," + 
          "    \"breakDownStructure\": [" + 
          "        \"%s\"," + 
          "        \"BA03\"," + 
          "        \"45A2\"" + 
          "    ]," + 
          "    \"ipCode\": \"IP68\"," + 
          "    \"shockResistance\": \"1128\"," + 
          "    \"measuringRange\": \"3-48\"," + 
          "    \"maintainee\": \"Max M.\"," + 
          "    \"location\": {" + 
          "        \"coordinateSystem\": \"GK 3 [EPSG:42500]\"," + 
          "        \"x\": \"3256578.2542\"," + 
          "        \"y\": \"525365.1252\"," + 
          "        \"z\": \"1010.2542\"," + 
          "        \"description\": null" + 
          "    }," + 
          "    \"dataseries\": [" + 
          "        {" + 
          "            \"unitOfMeasurement\": \"%s\"," + 
          "            \"calibrationValue\": \"7 mm/s\"" + 
          "        }" + 
          "    ]," + 
          "    \"alertLevel\": {" + 
          "        \"l1High\": 2," + 
          "        \"l2High\": 3" + 
          "    }" + 
          "}", expectedBreakdownStructure, expectedUnitOfMeasurement);
      
  
      // When:
      JsonObject actualData = toJsonObject(underTest.updateDeviceData(expectedOldValues, toJsonObject(updateData)));
  
      // Then:
      JsonObject actualMetadata = actualData.getAsJsonObject("metadata");
      assertThat(actualMetadata.getAsJsonObject("Installation").get("BreakDownStructure1").getAsString()).isEqualTo(expectedBreakdownStructure);
      
      JsonArray dataseries = actualMetadata.getAsJsonArray("Dataseries");
      assertThat(dataseries).hasSize(1);
      JsonObject firstDataseries = dataseries.get(0).getAsJsonObject();
      assertThat(firstDataseries.get("Id").getAsString()).isEqualTo(expectedDataseriesId);
      assertThat(firstDataseries.get("UnitOfMeasurement").getAsString()).isEqualTo(expectedUnitOfMeasurement);
    }
  
  
  @Test
    public void testUpdateDeviceData_theOriginalNumberOfDataseriesObjectsIskept() throws Exception {
      // Given:
      String expectedDataseriesId1 = "ACCELERATION";
      String expectedDataseriesId2 = "ACC";
      String expectedOldValues = String.format("{" + 
          "    \"clientId\": \"d:jbba0g:accelerometer:accelerometerffm\"," + 
          "    \"typeId\": \"accelerometer\"," + 
          "    \"deviceId\": \"accelerometerffm\"," + 
          "    \"deviceInfo\": {}," + 
          "    \"metadata\": {" + 
          "        \"Installation\": {" + 
          "            \"InstallationDate\": \"2019-07-09T10:34:54.164\"," + 
          "            \"FirstMeasurement\": \"2019-08-09T11:44:50.832\"," + 
          "            \"BreakDownStructure1\": \"30A1\"," + 
          "            \"BreakDownStructure2\": \"BA03\"," + 
          "            \"BreakDownStructure3\": \"45A2\"" + 
          "        }," + 
          "        \"Technical Info\": {" + 
          "            \"Maintainee\": \"Max M.\"," + 
          "            \"ManufacturingDate\": \"1978-12-01\"," + 
          "            \"IPCode\": \"IP68\"," + 
          "            \"Manufacturer\": \"Siemens\"," + 
          "            \"SensorType\": \"R2-D2\"," + 
          "            \"ShockResistance\": \"1128\"," + 
          "            \"Measuring Range\": \"3-48\"" + 
          "        }," + 
          "        \"Coordinates\": {" + 
          "            \"CoordinateSystem\": \"GK 3 [EPSG:42500]\"," + 
          "            \"CoordinateX\": \"3256578.2542\"," + 
          "            \"CoordinateY\": \"525365.1252\"," + 
          "            \"CoordinateZ\": \"1010.2542\"" + 
          "        }," + 
          "        \"Dataseries\": [" + 
          "            {" + 
          "                \"Id\": \"%s\"," + 
          "                \"Title\": \"Acceleration\"," + 
          "                \"UnitOfMeasurement\": \"mm/s\"," + 
          "                \"SamplingRate\": 100," + 
          "                \"CalibrationValue\": \"7 mm/s\"" + 
          "            }," + 
          "            {" + 
          "                \"Id\": \"%s\"," + 
          "                \"Title\": \"Acceleration2\"," + 
          "                \"UnitOfMeasurement\": \"mm/s\"," + 
          "                \"SamplingRate\": 100," + 
          "                \"CalibrationValue\": \"7 mm/s\"" + 
          "            }" + 
          "        ]," + 
          "        \"Identification\": {" + 
          "            \"DeviceId\": \"accelerometerffm\"," + 
          "            \"ProjectId\": \"1\"," + 
          "            \"Channel\": \"30T5\"," + 
          "            \"TransferRate\": 50" + 
          "        }," + 
          "        \"AlertLevel\": {" + 
          "            \"L1High\": \"2\"," + 
          "            \"L2High\": \"3\"" + 
          "        }" + 
          "    }," + 
          "    \"registration\": {" + 
          "        \"auth\": {" + 
          "            \"id\": \"g:jbba0g:Sensorbox:Sensorbox02\"," + 
          "            \"type\": \"dev\"" + 
          "        }," + 
          "        \"date\": \"2019-07-02T13:06:58.922Z\"" + 
          "    }," + 
          "    \"groups\": [" + 
          "        \"gw_def_res_grp:jbba0g:Sensorbox:Sensorbox02\"" + 
          "    ]," + 
          "    \"gatewayId\": \"Sensorbox02\"," + 
          "    \"gatewayTypeId\": \"Sensorbox\"," + 
          "    \"refs\": {" + 
          "        \"diag\": {" + 
          "            \"logs\": \"/api/v0002/device/types/accelerometer/devices/accelerometerffm/diag/logs\"," + 
          "            \"errorCodes\": \"/api/v0002/device/types/accelerometer/devices/accelerometerffm/diag/errorCodes\"" + 
          "        }," + 
          "        \"location\": \"/api/v0002/device/types/accelerometer/devices/accelerometerffm/location\"," + 
          "        \"clientState\": \"/api/v0002/clientconnectionstates/d:jbba0g:accelerometer:accelerometerffm\"" + 
          "    }" + 
          "}", expectedDataseriesId1, expectedDataseriesId2);
      String updateData = "{" + 
          "    \"channel\": \"30T5\"," + 
          "    \"breakDownStructure\": [" + 
          "        \"30A11\"," + 
          "        \"BA03\"," + 
          "        \"45A2\"" + 
          "    ]," + 
          "    \"ipCode\": \"IP68\"," + 
          "    \"shockResistance\": \"1128\"," + 
          "    \"measuringRange\": \"3-48\"," + 
          "    \"maintainee\": \"Max M.\"," + 
          "    \"location\": {" + 
          "        \"coordinateSystem\": \"GK 3 [EPSG:42500]\"," + 
          "        \"x\": \"3256578.2542\"," + 
          "        \"y\": \"525365.1252\"," + 
          "        \"z\": \"1010.2542\"," + 
          "        \"description\": null" + 
          "    }," + 
          "    \"dataseries\": [" + 
          "        {" + 
          "            \"unitOfMeasurement\": \"m\"," + 
          "            \"calibrationValue\": \"7 mm/s\"" + 
          "        }" + 
          "    ]," + 
          "    \"alertLevel\": {" + 
          "        \"l1High\": 2," + 
          "        \"l2High\": 3" + 
          "    }" + 
          "}";
      
  
      // When:
      JsonObject actualData = toJsonObject(underTest.updateDeviceData(expectedOldValues, toJsonObject(updateData)));
  
      // Then:
      JsonObject actualMetadata = actualData.getAsJsonObject("metadata");
      
      JsonArray dataseries = actualMetadata.getAsJsonArray("Dataseries");
      assertThat(dataseries).hasSize(2);
      JsonObject firstDataseries = dataseries.get(0).getAsJsonObject();
      assertThat(firstDataseries.get("Id").getAsString()).isEqualTo(expectedDataseriesId1);
      JsonObject secondDataseries = dataseries.get(1).getAsJsonObject();
      assertThat(secondDataseries.get("Id").getAsString()).isEqualTo(expectedDataseriesId2);
    }
  
}
