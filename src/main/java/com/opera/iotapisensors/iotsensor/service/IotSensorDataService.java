package com.opera.iotapisensors.iotsensor.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opera.iotapisensors.events.DomainEventPublisher;
import com.opera.iotapisensors.iotsensor.dto.AccelerometerDto;
import com.opera.iotapisensors.iotsensor.dto.CrackSensorDto;
import com.opera.iotapisensors.iotsensor.dto.DataDto;
import com.opera.iotapisensors.iotsensor.dto.DeviceDataDto;
import com.opera.iotapisensors.iotsensor.dto.DeviceDataGraphDto;
import com.opera.iotapisensors.iotsensor.dto.HighLowValuesDto;
import com.opera.iotapisensors.iotsensor.dto.InclinometerDataDto;
import com.opera.iotapisensors.iotsensor.dto.IotSensorDto;
import com.opera.iotapisensors.iotsensor.dto.SensorDataDto;
import com.opera.iotapisensors.iotsensor.entities.AbstractCrackSensorData;
import com.opera.iotapisensors.iotsensor.entities.AbstractTemperatureSensorData;
import com.opera.iotapisensors.iotsensor.entities.AccelerometerData;
import com.opera.iotapisensors.iotsensor.entities.DataseriesValues;
import com.opera.iotapisensors.iotsensor.entities.ImuData;
import com.opera.iotapisensors.iotsensor.entities.IotCrackSensorData;
import com.opera.iotapisensors.iotsensor.entities.IotInclinometerData;
import com.opera.iotapisensors.iotsensor.entities.TemperatureData;
import com.opera.iotapisensors.iotsensor.enums.BaseDataseries;
import com.opera.iotapisensors.iotsensor.enums.DeviceType;
import com.opera.iotapisensors.iotsensor.repository.AccelerometerDataRepository;
import com.opera.iotapisensors.iotsensor.repository.CrackdisplacementDataRepository;
import com.opera.iotapisensors.iotsensor.repository.ImuDataRepository;
import com.opera.iotapisensors.iotsensor.repository.InclinometerDataRepository;
import com.opera.iotapisensors.iotsensor.repository.TemperatureDataRepository;

@Service
public class IotSensorDataService {

  @Autowired
  private CrackdisplacementDataRepository crackdisplacementRepository;

  @Autowired
  private AccelerometerDataRepository accelerometerDataRepository;

  @Autowired
  private InclinometerDataRepository inclinometerDataRepository;

  @Autowired
  private ImuDataRepository imuDataRepository;

  @Autowired
  private TemperatureDataRepository temperatureDataRepository;

  @Autowired
  private DeviceManagementService deviceManagementService;

  @Autowired
  private DomainEventPublisher domainEventPublisher;

  @Autowired
  private WatsonIotPlatformGateway iotPlatformGateway;



  // Handling of timestamps between FE local time (Europe/Berlin) to BE database time (UTC) and back
  static final TemporalAmount INC_INTERVAL = Duration.of(18750, ChronoUnit.MILLIS);
  static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");
  // TODO: Workaround: In the future, the time zone of the sensor is to be configured in the
  // metadata and will be used here instead of "Europe/Berlin".
  static final ZoneId ZONE_ID_LOCAL = ZoneId.of("Europe/Berlin");
  static final long DIFF_MINUTES_LOCAL_TO_UTC =
      ZonedDateTime.now(ZONE_ID_LOCAL).getOffset().getTotalSeconds() / 60;
  static final DateTimeFormatter RESP_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
  static final String MEASUREMENT_UNIT_DEGREE = "degree";
  static final String MEASUREMENT_UNIT_ACCELERATION = "m/s^2";
  static final String MIN_VIEW = "minview";
  static final String MAX_VIEW = "maxview";

  @Value("${spring.rabbitmq.routingKey}")
  public String routingKey;

  private Logger logger = LoggerFactory.getLogger(IotSensorDataService.class);


  public LocalDateTime getLastMeasurementDateTime(DeviceType deviceType, String deviceId) {
    switch(deviceType) {
      case ACCELEROMETER:
        return accelerometerDataRepository.getDateOfLatestEntryByDevice(deviceId);
      case CRACK:
        return crackdisplacementRepository.getDataOfLatestEntryByDevice(deviceId);
      case IMU:
        return imuDataRepository.getDateOfLatestEntryByDevice(deviceId);
      case INCLINOMETER:
        return inclinometerDataRepository.getDateOfLatestEntryByDevice(deviceId);
      case TEMPERATURE:
        return temperatureDataRepository.getDateOfLatestEntryByDevice(deviceId);
      default:
        throw new IllegalArgumentException("Unknown deviceType: " + deviceType.name());
    }
  }

  public @Nullable DataseriesValues getDataseriesValuesByMeasurementDate(DeviceType deviceType, String deviceId, LocalDateTime measuredAt) {
    DataseriesValues result = null;
    switch(deviceType) {
      case ACCELEROMETER:
        List<AccelerometerData> accelerometerData = accelerometerDataRepository.getDataByDeviceIdAndMeasuredAt(deviceId, measuredAt);
        if (!accelerometerData.isEmpty()) {
          result = accelerometerData.get(0);
        }
        break;
      case CRACK:
        List<IotCrackSensorData> crackData = crackdisplacementRepository.getDataByDeviceIdAndMeasuredAt(deviceId, measuredAt);
        if (!crackData.isEmpty()) {
          result = crackData.get(0);
        }
        break;
      case IMU:
        List<ImuData> imuData = imuDataRepository.getDataByDeviceIdAndMeasuredAt(deviceId, measuredAt);
        if (!imuData.isEmpty()) {
          result = imuData.get(0);
        }
        break;
      case INCLINOMETER:
        List<IotInclinometerData> inclinometerData = inclinometerDataRepository.getDataByDeviceIdAndMeasuredAt(deviceId, measuredAt);
        if (!inclinometerData.isEmpty()) {
          result = inclinometerData.get(0);
        }
        break;
      case TEMPERATURE:
        List<TemperatureData> temperatureData = temperatureDataRepository.getDataByDeviceIdAndMeasuredAt(deviceId, measuredAt);
        if (!temperatureData.isEmpty()) {
          result = temperatureData.get(0);
        }
        break;
      default:
        throw new IllegalArgumentException("Unknown deviceType: " + deviceType.name());
    }
    return result;
  }

  /**
   * Gets the high low values crackdisplacement.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @param deviceId the device id
   * @return the high low values crackdisplacement
   */
  public CrackSensorDto getHighLowValuesCrackdisplacement(LocalDateTime startDate,
      LocalDateTime endDate, String deviceId) {
    CrackSensorDto highLowCrackSensor = new CrackSensorDto();
    HighLowValuesDto highLowValues = new HighLowValuesDto();
    AbstractCrackSensorData lowValueAndTimestamp;
    AbstractCrackSensorData highValueAndTimestamp;

      lowValueAndTimestamp = crackdisplacementRepository.getLowValue(startDate, endDate, deviceId);
      highValueAndTimestamp = crackdisplacementRepository.getHighValue(startDate, endDate, deviceId);

    if (lowValueAndTimestamp != null && highValueAndTimestamp != null) {
      highLowValues.setLowValue(lowValueAndTimestamp.getCrack_value());
      highLowValues.setLowTimestamp(RESP_FORMATTER.format(lowValueAndTimestamp.getMeasuredAt()));
      highLowValues.setHighValue(highValueAndTimestamp.getCrack_value());
      highLowValues.setHighTimestamp(RESP_FORMATTER.format(highValueAndTimestamp.getMeasuredAt()));
      highLowCrackSensor.setHighLowValues_crack(highLowValues);
    }

    return highLowCrackSensor;
  }

  /**
   * Gets the high low values accelerometer.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @param deviceId the device id
   * @return the high low values accelerometer
   */
  public AccelerometerDto getHighLowValuesAccelerometer(LocalDateTime startDate,
      LocalDateTime endDate, String deviceId) {
    AccelerometerDto highLowValuesAcc = new AccelerometerDto();
    HighLowValuesDto highLowValues = new HighLowValuesDto();

    AccelerometerData lowValueAndTimpestamp =
        accelerometerDataRepository.getLowValue(startDate, endDate, deviceId);

    AccelerometerData highValueAndTimestamp =
        accelerometerDataRepository.getHighValue(startDate, endDate, deviceId);

    if (lowValueAndTimpestamp != null && highValueAndTimestamp != null) {
      highLowValues.setLowValue(lowValueAndTimpestamp.getValue1());
      highLowValues.setLowTimestamp(RESP_FORMATTER.format(lowValueAndTimpestamp.getMeasuredAt()));
      highLowValues.setHighValue(highValueAndTimestamp.getValue1());
      highLowValues.setHighTimestamp(RESP_FORMATTER.format(highValueAndTimestamp.getMeasuredAt()));
      highLowValuesAcc.setHighLowValues_x(highLowValues);
    }

    return highLowValuesAcc;
  }


  public IotSensorDto getCrackDataByDeviceIdWithTimeSpan(String deviceId, LocalDateTime startDate,
      LocalDateTime endDate) {

    List<? extends AbstractCrackSensorData> data = crackdisplacementRepository.getCrackdisplacementDataByDeviceId(startDate, endDate, deviceId);

    IotSensorDto sensorDataForDevice = new IotSensorDto();
    List<SensorDataDto> sensorDataList = new ArrayList<>();
    List<DataDto> crackDataList = new ArrayList<>();
    SensorDataDto sensorData = new SensorDataDto();

    if (!data.isEmpty()) {
      sensorDataForDevice.setDeviceType("Crack Sensor");
    }
    sensorDataForDevice.setDeviceId(deviceId);
    sensorData.setTitle("crack");
    sensorData.setUnitOfMeasurement("mm");

    for (AbstractCrackSensorData crackDisplacementData : data) {
      DataDto crackData = new DataDto();
      sensorDataForDevice.setDeviceId(crackDisplacementData.getDeviceId());

      crackData.setTimestamp(diffUtcToLocalString(crackDisplacementData.getMeasuredAt()));
      crackData.setValue(crackDisplacementData.getCrack_value());

      crackDataList.add(crackData);
    }
    sensorData.setData(crackDataList);
    sensorDataList.add(sensorData);
    sensorDataForDevice.setSensorData(sensorDataList);

    return sensorDataForDevice;
  }


  public IotSensorDto getInclinometerDataByDeviceIdWithTimeSpan(String deviceId,
      LocalDateTime startDate, LocalDateTime endDate) {

    List<IotInclinometerData> data =
        inclinometerDataRepository.getInclinometerDataWithTimeSpan(deviceId, startDate, endDate);

    IotSensorDto sensorDataForDevice = new IotSensorDto();
    List<SensorDataDto> sensorDataList = new ArrayList<>();
    List<DataDto> inclinometerDataListRoll = new ArrayList<>();
    List<DataDto> inclinometerDataListPitch = new ArrayList<>();
    List<DataDto> inclinometerDataListRollMavg = new ArrayList<>();
    List<DataDto> inclinometerDataListPitchMavg = new ArrayList<>();
    SensorDataDto sensorDataRoll = new SensorDataDto();
    SensorDataDto sensorDataPitch = new SensorDataDto();
    SensorDataDto sensorDataRollMavg = new SensorDataDto();
    SensorDataDto sensorDataPitchMavg = new SensorDataDto();

    if (!data.isEmpty()) {
      sensorDataForDevice.setDeviceType(data.get(0).getDeviceType());
    }
    sensorDataForDevice.setDeviceId(deviceId);
    sensorDataRoll.setTitle("roll");
    sensorDataRoll.setUnitOfMeasurement(MEASUREMENT_UNIT_DEGREE);
    sensorDataPitch.setTitle("pitch");
    sensorDataPitch.setUnitOfMeasurement(MEASUREMENT_UNIT_DEGREE);
    sensorDataRollMavg.setTitle("rollMavg");
    sensorDataRollMavg.setUnitOfMeasurement(MEASUREMENT_UNIT_DEGREE);
    sensorDataPitchMavg.setTitle("pitchMavg");
    sensorDataPitchMavg.setUnitOfMeasurement(MEASUREMENT_UNIT_DEGREE);

    for (IotInclinometerData inclinometerData : data) {
      ZonedDateTime timestamp = inclinometerData.getMeasuredAt().atZone(ZONE_ID_UTC);
      sensorDataForDevice.setDeviceType(inclinometerData.getDeviceType());
      sensorDataForDevice.setDeviceId(inclinometerData.getDeviceId());

      DataDto inclinometerDataRoll = new DataDto();
      inclinometerDataRoll.setTimestamp(diffUtcToLocalString(timestamp));
      inclinometerDataRoll.setValue(convertRadToDegree(inclinometerData.getRollValue()));

      inclinometerDataListRoll.add(inclinometerDataRoll);

      DataDto inclinometerDataPitch = new DataDto();
      inclinometerDataPitch.setTimestamp(diffUtcToLocalString(timestamp));
      inclinometerDataPitch.setValue(convertRadToDegree(inclinometerData.getPitchValue()));

      inclinometerDataListPitch.add(inclinometerDataPitch);

      DataDto inclinometerDataRollMavg = new DataDto();
      inclinometerDataRollMavg.setTimestamp(diffUtcToLocalString(timestamp));
      inclinometerDataRollMavg.setValue(convertRadToDegree(inclinometerData.getRollMavg()));

      inclinometerDataListRollMavg.add(inclinometerDataRollMavg);

      DataDto inclinometerDataPitchMavg = new DataDto();
      inclinometerDataPitchMavg.setTimestamp(diffUtcToLocalString(timestamp));
      inclinometerDataPitchMavg.setValue(convertRadToDegree(inclinometerData.getPitchMavg()));

      inclinometerDataListPitchMavg.add(inclinometerDataPitchMavg);
    }

    sensorDataRoll.setData(inclinometerDataListRoll);
    sensorDataPitch.setData(inclinometerDataListPitch);
    sensorDataRollMavg.setData(inclinometerDataListRollMavg);
    sensorDataPitchMavg.setData(inclinometerDataListPitchMavg);

    sensorDataList.add(sensorDataRoll);
    sensorDataList.add(sensorDataPitch);
    sensorDataList.add(sensorDataRollMavg);
    sensorDataList.add(sensorDataPitchMavg);

    sensorDataForDevice.setSensorData(sensorDataList);

    return sensorDataForDevice;
  }


  public IotSensorDto getAccelerometerDataByDeviceIdWithTimeSpan(String deviceId,
      LocalDateTime startDate, LocalDateTime endDate) {
    IotSensorDto sensorDataForDevice = new IotSensorDto();
    List<SensorDataDto> sensorDataList = new ArrayList<>();
    List<DataDto> accelerometerDataList = new ArrayList<>();
    SensorDataDto sensorData = new SensorDataDto();

    List<AccelerometerData> data = accelerometerDataRepository
        .getAccelerometerDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate);

    sensorDataForDevice.setDeviceId(deviceId);
    sensorData.setTitle("acceleration");
    sensorData.setUnitOfMeasurement("mm/s^2");
    if (!data.isEmpty()) {
      sensorDataForDevice.setDeviceType(data.get(0).getDeviceType());
    }

    for (AccelerometerData accelerometerData : data) {
      DataDto accData = new DataDto();

      accData.setTimestamp(diffUtcToLocalString(accelerometerData.getMeasuredAt()));
      accData.setValue(accelerometerData.getValue1());

      accelerometerDataList.add(accData);
    }
    sensorData.setData(accelerometerDataList);
    sensorDataList.add(sensorData);
    sensorDataForDevice.setSensorData(sensorDataList);

    return sensorDataForDevice;
  }


  public IotSensorDto getImuDataByDeviceIdWithTimeSpan(String deviceId, LocalDateTime startDate,
      LocalDateTime endDate) {
    IotSensorDto sensorDataForDevice = new IotSensorDto();
    List<SensorDataDto> sensorDataList = new ArrayList<>();
    List<DataDto> inclinometerDataListRoll = new ArrayList<>();
    List<DataDto> inclinometerDataListPitch = new ArrayList<>();
    List<DataDto> inclinometerDataListYaw = new ArrayList<>();
    List<DataDto> inclinometerDataListAccX = new ArrayList<>();
    List<DataDto> inclinometerDataListAccY = new ArrayList<>();
    List<DataDto> inclinometerDataListAccZ = new ArrayList<>();
    List<DataDto> inclinometerDataListTemp = new ArrayList<>();
    SensorDataDto sensorDataRoll = new SensorDataDto();
    SensorDataDto sensorDataPitch = new SensorDataDto();
    SensorDataDto sensorDataYaw = new SensorDataDto();
    SensorDataDto sensorDataAccX = new SensorDataDto();
    SensorDataDto sensorDataAccY = new SensorDataDto();
    SensorDataDto sensorDataAccZ = new SensorDataDto();
    SensorDataDto sensorDataTemp = new SensorDataDto();

    List<ImuData> data =
        imuDataRepository.getImuDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate);
    sensorDataForDevice.setDeviceId(deviceId);
    if (!data.isEmpty()) {
      sensorDataForDevice.setDeviceType(data.get(0).getDeviceType());
    }
    sensorDataRoll.setTitle("roll");
    sensorDataRoll.setUnitOfMeasurement("rad");
    sensorDataPitch.setTitle("pitch");
    sensorDataPitch.setUnitOfMeasurement("rad");
    sensorDataYaw.setTitle("yaw");
    sensorDataYaw.setUnitOfMeasurement("rad");
    sensorDataAccX.setTitle("accelerationX");
    sensorDataAccX.setUnitOfMeasurement(MEASUREMENT_UNIT_ACCELERATION);
    sensorDataAccY.setTitle("accelerationY");
    sensorDataAccY.setUnitOfMeasurement(MEASUREMENT_UNIT_ACCELERATION);
    sensorDataAccZ.setTitle("accelerationZ");
    sensorDataAccZ.setUnitOfMeasurement(MEASUREMENT_UNIT_ACCELERATION);
    sensorDataTemp.setTitle("temperature");
    sensorDataTemp.setUnitOfMeasurement(MEASUREMENT_UNIT_DEGREE);

    for (ImuData imuData : data) {
      DataDto inclinometerDataRoll = new DataDto();
      inclinometerDataRoll.setTimestamp(diffUtcToLocalString(imuData.getMeasuredAt()));
      inclinometerDataRoll.setValue(imuData.getRoll());
      inclinometerDataListRoll.add(inclinometerDataRoll);

      DataDto inclinometerDataPitch = new DataDto();
      inclinometerDataPitch.setTimestamp(diffUtcToLocalString(imuData.getMeasuredAt()));
      inclinometerDataPitch.setValue(imuData.getPitch());
      inclinometerDataListPitch.add(inclinometerDataPitch);

      DataDto inclinometerDataYaw = new DataDto();
      inclinometerDataYaw.setTimestamp(diffUtcToLocalString(imuData.getMeasuredAt()));
      inclinometerDataYaw.setValue(imuData.getYaw());
      inclinometerDataListYaw.add(inclinometerDataYaw);

      DataDto inclinometerDataAccX = new DataDto();
      inclinometerDataAccX.setTimestamp(diffUtcToLocalString(imuData.getMeasuredAt()));
      inclinometerDataAccX.setValue(imuData.getAccelerationX());
      inclinometerDataListAccX.add(inclinometerDataAccX);

      DataDto inclinometerDataAccY = new DataDto();
      inclinometerDataAccY.setTimestamp(diffUtcToLocalString(imuData.getMeasuredAt()));
      inclinometerDataAccY.setValue(imuData.getAccelerationY());
      inclinometerDataListAccY.add(inclinometerDataAccY);

      DataDto inclinometerDataAccZ = new DataDto();
      inclinometerDataAccZ.setTimestamp(diffUtcToLocalString(imuData.getMeasuredAt()));
      inclinometerDataAccZ.setValue(imuData.getAccelerationZ());
      inclinometerDataListAccZ.add(inclinometerDataAccZ);

      DataDto inclinometerDataTemp = new DataDto();
      inclinometerDataTemp.setTimestamp(diffUtcToLocalString(imuData.getMeasuredAt()));
      inclinometerDataTemp.setValue(imuData.getTemperature());
      inclinometerDataListTemp.add(inclinometerDataTemp);
    }

    sensorDataRoll.setData(inclinometerDataListRoll);
    sensorDataPitch.setData(inclinometerDataListPitch);
    sensorDataYaw.setData(inclinometerDataListYaw);
    sensorDataAccX.setData(inclinometerDataListAccX);
    sensorDataAccY.setData(inclinometerDataListAccY);
    sensorDataAccZ.setData(inclinometerDataListAccZ);
    sensorDataTemp.setData(inclinometerDataListTemp);

    sensorDataList.add(sensorDataRoll);
    sensorDataList.add(sensorDataPitch);
    sensorDataList.add(sensorDataYaw);
    sensorDataList.add(sensorDataAccX);
    sensorDataList.add(sensorDataAccY);
    sensorDataList.add(sensorDataAccZ);
    sensorDataList.add(sensorDataTemp);

    sensorDataForDevice.setSensorData(sensorDataList);

    return sensorDataForDevice;
  }


  public IotSensorDto getTemperatureDataByDeviceIdWithTimeSpan(String deviceId,
      LocalDateTime startDate, LocalDateTime endDate) {
    IotSensorDto sensorDataForDevice = new IotSensorDto();
    List<SensorDataDto> sensorDataList = new ArrayList<>();
    List<DataDto> tempDataListTemperature = new ArrayList<>();
    List<DataDto> tempDataListHumidity = new ArrayList<>();
    SensorDataDto sensorDataTemperature = new SensorDataDto();
    SensorDataDto sensorDataHumidity = new SensorDataDto();
    List<? extends AbstractTemperatureSensorData> data;

    data = temperatureDataRepository.getTemperatureDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate);

    sensorDataForDevice.setDeviceId(deviceId);
    if (!data.isEmpty()) {
      sensorDataForDevice.setDeviceType(data.get(0).getDeviceType());
    }

    sensorDataTemperature.setTitle("temperature");
    sensorDataTemperature.setUnitOfMeasurement("degree celcius");
    sensorDataHumidity.setTitle("humidity");
    sensorDataHumidity.setUnitOfMeasurement("percentage");

    for (AbstractTemperatureSensorData temperatureData : data) {
      DataDto sensorDataTemp = new DataDto();
      sensorDataTemp.setTimestamp(diffUtcToLocalString(temperatureData.getMeasuredAt()));
      sensorDataTemp.setValue(temperatureData.getTemperature());
      tempDataListTemperature.add(sensorDataTemp);

      DataDto sensorDataHum = new DataDto();
      sensorDataHum.setTimestamp(diffUtcToLocalString(temperatureData.getMeasuredAt()));
      sensorDataHum.setValue(temperatureData.getHumidity());
      tempDataListHumidity.add(sensorDataHum);
    }

    sensorDataTemperature.setData(tempDataListTemperature);
    sensorDataHumidity.setData(tempDataListHumidity);

    sensorDataList.add(sensorDataTemperature);
    sensorDataList.add(sensorDataHumidity);

    sensorDataForDevice.setSensorData(sensorDataList);

    return sensorDataForDevice;
  }


  public List<SensorDataDto> getCrackSensorDataByDeviceId(String deviceId, String size) {

    // get the latest measurement value
    LocalDateTime endDate = crackdisplacementRepository.getDataOfLatestEntryByDevice(deviceId);
    if (endDate == null)
      return null;
    LocalDateTime startDate = null;

    if (size.equals(MAX_VIEW)) {
      startDate = endDate.minusMinutes(15);
    } else if (size.equals(MIN_VIEW)) {
      startDate = endDate.minusMinutes(5);
    }

    IotSensorDto crackData = getCrackDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate);

    return crackData.getSensorData();
  }


  public List<SensorDataDto> getAccelerometerDataByDeviceId(String deviceId, String size) {

    // get the last measurement value
    LocalDateTime endDate = accelerometerDataRepository.getDateOfLatestEntryByDevice(deviceId);
    if (endDate == null)
      return null;
    LocalDateTime startDate = null;

    if (size.equals(MAX_VIEW)) {
      startDate = endDate.minusMinutes(15);
    } else if (size.equals(MIN_VIEW)) {
      startDate = endDate.minusMinutes(5);
    }

    IotSensorDto accelerometerData =
        getAccelerometerDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate);

    return accelerometerData.getSensorData();
  }

  public List<SensorDataDto> getInclinometerDataByDeviceIdMinview(String deviceId) {

    // get the last measurement value
    LocalDateTime endDate =
        inclinometerDataRepository.getDateOfLatestEntryByDevice(deviceId);
    if (endDate == null)
      return null;
    LocalDateTime startDate = endDate.minusMinutes(5);

    IotSensorDto inclinometerData =
        getInclinometerDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate);

    return inclinometerData.getSensorData();
  }

  /**
   * Get the inclinometer values of a given time frame.
   *
   * @param startDate
   * @param endDate
   * @param deviceId
   * @return a list of all values found in between start and end date
   */
  public List<InclinometerDataDto> getInclinometerDataByDeviceIdWithTimeSpan(
      LocalDateTime startDate, LocalDateTime endDate, String deviceId) {
    List<InclinometerDataDto> result = new ArrayList<>();
    int id = 1;
    List<IotInclinometerData> data =
        inclinometerDataRepository.getInclinometerDataWithTimeSpan(deviceId, startDate, endDate);

    for (IotInclinometerData iotIncData : data) {
      ZonedDateTime timestamp = iotIncData.getMeasuredAt().atZone(ZONE_ID_UTC);
      Float rollValue = convertRadToDegree(iotIncData.getRollValue());
      Float pitchValue = convertRadToDegree(iotIncData.getPitchValue());
      Float rollMavg = convertRadToDegree(iotIncData.getRollMavg());
      Float pitchMavg = convertRadToDegree(iotIncData.getPitchMavg());

      result.add(new InclinometerDataDto(id, diffUtcToLocalString(timestamp), rollValue, pitchValue,
          rollMavg, pitchMavg));
      id++;
    }

    return result;
  }

  /**
   * Get the device configuration from the Watson IoT platform.
   *
   * @param deviceType type of the device
   * @param deviceId unique identification string of the device
   * @param endDate
   * @param startDate
   * @param size
   * @return the object that is stored in the platform
   */
  public DeviceDataDto getDeviceConfiguration(DeviceType deviceType, String deviceId,
      LocalDateTime startDate, LocalDateTime endDate, String size) {
    DeviceDataDto result = new DeviceDataDto();

    // get metadata from Watson IoT platform device management
    result = deviceManagementService.getDeviceConfiguration(deviceType, deviceId);

    if (result != null) {
      result.setLastMeasurement(getLastMeasurementDateTime(deviceType, deviceId));
      // add data series within given timeframe between startDate and endDate
      if (startDate != null && endDate != null) {
        addDataseriesValues(result.getDataseries(), deviceType, deviceId, diffLocalToUtc(startDate),
            diffLocalToUtc(endDate));
      }

      addFirstAndLastMeasurementValues(result.getDataseries(), deviceType, deviceId, result.getFirstMeasurement(), result.getLastMeasurement());

      result.setAlertLevelStatus(iotPlatformGateway.getAlertLevel(deviceType, deviceId));

      // add data for the graph view
      if (size != null) {
        result.setGraph(setGraphWithSize(deviceType, size, deviceId));
      }
    }

    return result;
  }

  private List<DataseriesValues> getValues(DeviceType deviceType, String deviceId,
      LocalDateTime startDate, LocalDateTime endDate) {
    List<DataseriesValues> result = new ArrayList<DataseriesValues>();

    switch (deviceType) {
      case ACCELEROMETER: {
        result.addAll(accelerometerDataRepository
            .getAccelerometerDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate));
        break;
      }
      case INCLINOMETER: {
        result.addAll(inclinometerDataRepository.getInclinometerDataWithTimeSpan(deviceId,
            startDate, endDate));
        break;
      }
      case CRACK: {
          result.addAll(crackdisplacementRepository.getCrackdisplacementDataByDeviceId(startDate, endDate, deviceId));
        break;
      }
      case IMU: {
        result.addAll(
            imuDataRepository.getImuDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate));
        break;
      }
      case TEMPERATURE: {
        result.addAll(temperatureDataRepository.getTemperatureDataByDeviceIdWithTimeSpan(deviceId,
            startDate, endDate));
        break;
      }
      default:
        throw new IllegalArgumentException(
            "Data series not added, no relevant deviceType '" + deviceType + "' was found.");
    }

    return result;
  }

  private void addDataseriesValues(@Nullable List<SensorDataDto> dataseries, DeviceType deviceType,
      String deviceId, LocalDateTime startDate, LocalDateTime endDate) {
    if (dataseries == null || dataseries.isEmpty()) {
      return;
    }

    List<DataseriesValues> data = getValues(deviceType, deviceId, startDate, endDate);

    for(SensorDataDto ds : dataseries) {
      for (DataseriesValues d: data) {
        BaseDataseries dataseriesId = d.getDataseriesByName(ds.getId());
        DataDto dataDto = new DataDto(diffUtcToLocalString(d.getMeasuredAt()), d.getDataseriesValue(dataseriesId));
        ds.addData(dataDto);
      }
    }
  }

  private void addFirstAndLastMeasurementValues(@Nullable List<SensorDataDto> dataseries, DeviceType deviceType,
      String deviceId, LocalDateTime firstMeasurementDate, LocalDateTime lastMeasurementDate) {
    if (dataseries == null || dataseries.isEmpty()) {
      return;
    }

    DataseriesValues firstValues = getDataseriesValuesByMeasurementDate(deviceType, deviceId, firstMeasurementDate);
    DataseriesValues lastValues = getDataseriesValuesByMeasurementDate(deviceType, deviceId, lastMeasurementDate);
    for(SensorDataDto ds : dataseries) {
      BaseDataseries dataseriesId = null;
      if (firstValues != null) {
        dataseriesId = firstValues.getDataseriesByName(ds.getId());
        ds.setFirstMeasurementValue(firstValues.getDataseriesValue(dataseriesId));
      }
      if (lastValues != null) {
        if (dataseriesId == null) {
          dataseriesId = lastValues.getDataseriesByName(ds.getId());
        }
        ds.setLastMeasurementValue(lastValues.getDataseriesValue(dataseriesId));
      }
    }
  }


  /**
   * Update the device configuration in the Watson IoT platform.
   *
   * @param deviceType type of the device
   * @param deviceId unique identification string of the device
   * @param object the new device configuration
   * @return the object that has been stored in the platform
   */
  public DeviceDataDto putDeviceConfiguration(DeviceType deviceType, String deviceId,
      String deviceData) {

    JsonObject deviceDataJson = new JsonParser().parse(deviceData).getAsJsonObject();

    // Update the device configuration in the Watson IoT platform
    DeviceDataDto response =
        deviceManagementService.putDeviceConfiguration(deviceType, deviceId, deviceDataJson);

    if (response != null) {
      // Event publication: If the update was successful a message is emitted so that other services
      // who are subscribed can update their copy of device metadata.
      JsonObject jsonObject = createMetadataChangedMessage(deviceType, deviceId, deviceDataJson);

      try {
        domainEventPublisher.publishRabbitmqEvent(jsonObject.toString());
      } catch (Exception e) {
        logger.error("Error in DomainEventPublisher: {}", e.getMessage());
      }
    }

    return response;
  }

  public DeviceDataGraphDto getDeviceDataForGraph(DeviceType deviceType, String deviceId,
      LocalDateTime startDate, LocalDateTime endDate, String size) {
    DeviceDataGraphDto result = new DeviceDataGraphDto();
    result.setDeviceId(deviceId);
    result.setDeviceType(deviceType.getLabel());

    if (startDate != null && endDate != null) {
      result.setSensorData(setDataSeriesWithTimeSpan(deviceType, deviceId,
          diffLocalToUtc(startDate), diffLocalToUtc(endDate)));
    }
    // without query parameters the most recent 5 minutes are returned
    if (result.getSensorData() == null && size == null) {
      size = MIN_VIEW;
    }
    // add data for the graph view
    if (size != null) {
      result.setGraph(setGraphWithSize(deviceType, size, deviceId));
    }

    return result;
  }

  private List<SensorDataDto> setDataSeriesWithTimeSpan(DeviceType deviceType, String deviceId,
      LocalDateTime startDate, LocalDateTime endDate) {
    switch (deviceType) {
      case ACCELEROMETER: {
        return getAccelerometerDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate)
            .getSensorData();
      }
      case INCLINOMETER: {
        return getInclinometerDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate)
            .getSensorData();
      }
      case CRACK: {
        return getCrackDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate).getSensorData();
      }
      case IMU: {
        return getImuDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate).getSensorData();
      }
      case TEMPERATURE: {
        return getTemperatureDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate)
            .getSensorData();
      }
      default:
        logger.warn("Data series not added, no relevant deviceType {} was found.", deviceType);
        return null;
    }
  }

  private List<SensorDataDto> setGraphWithSize(DeviceType deviceType, String size, String deviceId) {
    switch (deviceType) {
      case CRACK: {
        return getCrackSensorDataByDeviceId(deviceId, size);
      }
      case ACCELEROMETER: {
        return getAccelerometerDataByDeviceId(deviceId, size);
      }
      case INCLINOMETER: {
        return getInclinometerDataByDeviceIdMinview(deviceId);
      }
      default:
        logger.debug("Size parameter is not defined for deviceType {}.", deviceType);
        return null;
    }
  }

  private JsonObject createMetadataChangedMessage(DeviceType deviceType, String deviceId,
      JsonObject deviceData) {
    JsonObject result = new JsonObject();
    result.addProperty("topic", routingKey);
    result.addProperty("deviceType", deviceType.getLabel());
    result.addProperty("deviceId", deviceId);
    result.addProperty("modifiedAt", ZonedDateTime.now(ZoneOffset.UTC).toString());
    result.add("properties", deviceData);
    return result;
  }

  public LocalDateTime diffLocalToUtc(LocalDateTime date) {
    return date.minusMinutes(DIFF_MINUTES_LOCAL_TO_UTC);
  }

  public String diffUtcToLocalString(LocalDateTime date) {
    return RESP_FORMATTER.format(date.plusMinutes(DIFF_MINUTES_LOCAL_TO_UTC));
  }

  public String diffUtcToLocalString(ZonedDateTime date) {
    return RESP_FORMATTER.format(date.plusMinutes(DIFF_MINUTES_LOCAL_TO_UTC));
  }

  private Float convertRadToDegree(Float value) {
    return value != null ? (float) (Math.round(value * 180 / Math.PI * 100.0) / 100.0) : null;
  }
}
