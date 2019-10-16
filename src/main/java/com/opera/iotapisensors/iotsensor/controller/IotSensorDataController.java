package com.opera.iotapisensors.iotsensor.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.opera.iotapisensors.iotsensor.dto.DeviceDataDto;
import com.opera.iotapisensors.iotsensor.dto.DeviceDataGraphDto;
import com.opera.iotapisensors.iotsensor.dto.HighLowValuesContainer;
import com.opera.iotapisensors.iotsensor.dto.SensorDto;
import com.opera.iotapisensors.iotsensor.enums.DeviceType;
import com.opera.iotapisensors.iotsensor.repository.CrackdisplacementDataRepository;
import com.opera.iotapisensors.iotsensor.service.IotSensorDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api
@RestController
public class IotSensorDataController {

  @Autowired
  CrackdisplacementDataRepository crackdisplacementDataRepository;

  @Autowired
  IotSensorDataService iotSensorDataService;

  private static final String SECTION = "Section A";
  private static final String STATUS = "Active";

  
  
  private void validateDeviceType(String deviceTypeLabel) {
    if (DeviceType.valueOfLabel(deviceTypeLabel) == null) {
      throw new IllegalArgumentException("Invalid deviceType: " + deviceTypeLabel);
    }
  }

  /**
   * Gets the iot sensors by project id.
   *
   * @param projectId the project id
   * @return the iot sensors by project id
   */
  @GetMapping(value = "/sensors/{projectId}")
  @ResponseBody
  public ResponseEntity<List<SensorDto>> getIotSensorsByProjectId(
      @PathVariable("projectId") String projectId) {

    try {

      List<SensorDto> sensorList = new ArrayList<>();

      var crackDisplacement = new SensorDto();
      crackDisplacement.setAlertLevel("low");
      crackDisplacement.setDataReceived(LocalDateTime.now().minusSeconds(30));
      crackDisplacement.setDeviceId("Rissfox Mini RissMini031864");
      crackDisplacement.setDeviceType("crack displacements");
      crackDisplacement.setLatestValue(0.0328f);
      crackDisplacement.setLocation(SECTION);
      crackDisplacement.setStatus(STATUS);
      crackDisplacement.setUnitOfMeasurement("mm");

      sensorList.add(crackDisplacement);

      return new ResponseEntity<>(sensorList, HttpStatus.OK);

    } catch (Exception e) {
      log.error(e.toString());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }
  
  
  /**
   * Get the device configuration from the Watson IoT platform.
   *
   * @param projectId the project id
   * @param deviceTypeLabel the device type
   * @param deviceId the device id
   * @param startDate the start date
   * @param endDate the end date
   * @param size request a specific graph with minview or maxview
   * @return the device configuration from the Watson Iot platform and sensor data as requested
   */
  @ApiOperation(value = "Return a device configuration from the Watson IoT platform",
      response = DeviceDataDto.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Device configuration found."),
      @ApiResponse(code = 401, message = "Unauthorized."),
      @ApiResponse(code = 404, message = "No device with that type and id found.")})
  @GetMapping(value = "/sensors/{projectId}/{deviceType}/{deviceId}")
  public ResponseEntity<DeviceDataDto> getDeviceConfiguration(
      // @formatter:off
      @PathVariable String projectId,
      @PathVariable("deviceType") String deviceTypeLabel,
      @PathVariable String deviceId,
      @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startDate,
      @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endDate,
      @RequestParam(name = "size", required = false) String size) {
    // @formatter:on
    log.debug("getDeviceConfiguration(projectId: {}, deviceType: {}, deviceId: {}, startDate: {}, endDate: {}, size: {})", projectId, deviceTypeLabel, deviceId, startDate, endDate, size);
    
    validateDeviceType(deviceTypeLabel);
    
    if ((startDate == null && endDate != null) || (startDate != null && endDate == null)
        || (startDate != null && endDate != null && endDate.compareTo(startDate) < 0)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    if (size != null && !(size.equals("minview") || size.equals("maxview"))) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    DeviceDataDto data =
        iotSensorDataService.getDeviceConfiguration(DeviceType.valueOfLabel(deviceTypeLabel), deviceId, startDate, endDate, size);

    return (data != null) ? new ResponseEntity<>(data, HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
  
  
  /**
   * Update the device configuration from the Watson IoT platform.
   *
   * @param projectId the project id
   * @param deviceTypeLabel the device type
   * @param deviceId the device id
   * @param object the data set that should be stored
   * @return the stored device configuration from the Watson Iot platform
   */
  @ApiOperation(value = "Return a updated device configuration from the Watson IoT platform",
      response = DeviceDataDto.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Device configuration updated."),
      @ApiResponse(code = 401, message = "Unauthorized."),
      @ApiResponse(code = 404, message = "No device with that type and id found.")})
  @PutMapping(value = "/sensors/{projectId}/{deviceType}/{deviceId}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<DeviceDataDto> putDeviceConfiguration(
      @PathVariable String projectId,
      @PathVariable("deviceType") String deviceTypeLabel,
      @PathVariable String deviceId,
      @RequestBody String deviceData) {
    
    validateDeviceType(deviceTypeLabel);

    DeviceDataDto data =
        iotSensorDataService.putDeviceConfiguration(DeviceType.valueOfLabel(deviceTypeLabel), deviceId, deviceData);

    return (data != null) ? new ResponseEntity<>(data, HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }
  

  /**
   * Gets the highest and lowest measurement value in the timefame now - 4 weeks for
   * crackdisplacement sensor.
   *
   * @param deviceId the device id
   * @return the high and low values for sensor
   * @throws JsonProcessingException the json processing exception
   */
  @ApiOperation(value = "", response = HighLowValuesContainer.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Min and may values found."),
      @ApiResponse(code = 204, message = "No data for min and max found."),
      @ApiResponse(code = 401, message = "Unauthorized.")})
  @GetMapping(value = "/sensors/{projectId}/{deviceType}/{deviceId}/minmax")
  @ResponseBody
  public ResponseEntity<HighLowValuesContainer> getHighAndLowValuesForSensor(
      @PathVariable String projectId,
      @PathVariable("deviceType") String deviceTypeLabel,
      @PathVariable String deviceId) throws JsonProcessingException {
    validateDeviceType(deviceTypeLabel);
    
    LocalDateTime endDate = LocalDateTime.now();
    LocalDateTime startDate = endDate.minusWeeks(4);
    
    DeviceType deviceType = DeviceType.valueOfLabel(deviceTypeLabel);

    HighLowValuesContainer highLow = null;
    if (DeviceType.CRACK.equals(deviceType)) {
      highLow = iotSensorDataService.getHighLowValuesCrackdisplacement(startDate, endDate, deviceId);
    } else if (DeviceType.ACCELEROMETER.equals(deviceType)) {
      highLow = iotSensorDataService.getHighLowValuesAccelerometer(startDate, endDate, deviceId);
    }

    return highLow != null ? new ResponseEntity<>(highLow, HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  
  // FIXME 2019.08.29, atavio: this here should be refactored because now is sending the same amount
  // of information as getDeviceConfiguration() when you provide startDate & endDate.. there are the
  // dataseries corrected and here not (some values hardcoded). Proposal next version would be
  // getDeviceConfiguration() returns the same as now but without dataseries.data and this call
  // should return only the dataseries object with the data property filled
  @GetMapping(value = "/sensors/{projectId}/{deviceType}/{deviceId}/graph")
  public ResponseEntity<DeviceDataGraphDto> getGraphForDevice(
      @PathVariable String projectId,
      @PathVariable("deviceType") String deviceTypeLabel,
      @PathVariable String deviceId,
      @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startDate,
      @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endDate,
      @RequestParam(name = "size", required = false) String size) {
    
    validateDeviceType(deviceTypeLabel);

    if ((startDate == null && endDate != null) || (startDate != null && endDate == null)
        || (startDate != null && endDate != null && endDate.compareTo(startDate) < 0)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    if (size != null && !(size.equals("minview") || size.equals("maxview"))) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    DeviceDataGraphDto graphData =
        iotSensorDataService.getDeviceDataForGraph(DeviceType.valueOfLabel(deviceTypeLabel), deviceId, startDate, endDate, size);

    return (graphData != null) ? new ResponseEntity<>(graphData, HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
  
  
}
