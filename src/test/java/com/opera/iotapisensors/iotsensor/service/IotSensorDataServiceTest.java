package com.opera.iotapisensors.iotsensor.service;

import static java.lang.Math.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.test.context.junit4.SpringRunner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opera.iotapisensors.events.DomainEventPublisher;
import com.opera.iotapisensors.iotsensor.dto.CrackSensorDto;
import com.opera.iotapisensors.iotsensor.dto.DeviceDataDto;
import com.opera.iotapisensors.iotsensor.dto.DeviceDataGraphDto;
import com.opera.iotapisensors.iotsensor.dto.HighLowValuesDto;
import com.opera.iotapisensors.iotsensor.dto.InclinometerDataDto;
import com.opera.iotapisensors.iotsensor.dto.IotSensorDto;
import com.opera.iotapisensors.iotsensor.dto.SensorDataDto;
import com.opera.iotapisensors.iotsensor.entities.AbstractCrackSensorData;
import com.opera.iotapisensors.iotsensor.entities.AccelerometerData;
import com.opera.iotapisensors.iotsensor.entities.ChCrackSensorData;
import com.opera.iotapisensors.iotsensor.entities.ChTemperatureData;
import com.opera.iotapisensors.iotsensor.entities.ImuData;
import com.opera.iotapisensors.iotsensor.entities.IotCrackSensorData;
import com.opera.iotapisensors.iotsensor.entities.IotInclinometerData;
import com.opera.iotapisensors.iotsensor.entities.TemperatureData;
import com.opera.iotapisensors.iotsensor.enums.AlertLevel;
import com.opera.iotapisensors.iotsensor.enums.BaseDataseries;
import com.opera.iotapisensors.iotsensor.enums.DeviceType;
import com.opera.iotapisensors.iotsensor.repository.AccelerometerDataRepository;
import com.opera.iotapisensors.iotsensor.repository.CrackdisplacementDataRepository;
import com.opera.iotapisensors.iotsensor.repository.ImuDataRepository;
import com.opera.iotapisensors.iotsensor.repository.InclinometerDataRepository;
import com.opera.iotapisensors.iotsensor.repository.TemperatureDataRepository;
import junit.framework.TestCase;

@RunWith(SpringRunner.class)
public class IotSensorDataServiceTest extends TestCase {

  @Mock
  InclinometerDataRepository inclinometerDataRepository;

  @Mock
  CrackdisplacementDataRepository crackdisplacementDataRepository;

  @Mock
  AccelerometerDataRepository accelerometerDataRepository;

  @Mock
  ImuDataRepository imuDataRepository;

  @Mock
  TemperatureDataRepository temperatureDataRepository;

  @Mock
  Logger logger = LoggerFactory.getLogger(IotSensorDataService.class);

  @Mock
  DomainEventPublisher domainEventPublisher;

  @Mock
  DeviceManagementService deviceManagementService;

  @InjectMocks
  IotSensorDataService iotSensorDataService;

  @Mock
  InclinometerDataDto inclinometerDataDto;

  @Mock
  CrackSensorDto crackSensorDto;

  @Mock
  HighLowValuesDto highLowValuesDto;

  @Mock
  IotCrackSensorData crackDisplacementData;

  @Mock
  WatsonIotPlatformGateway iotPlatformGateway;

  static final long DIFF_MINUTES_LOCAL_TO_UTC =
      ZonedDateTime.now(ZoneId.of("Europe/Berlin")).getOffset().getTotalSeconds() / 60;

  String sensorType = "R2-D2";
  String deviceId = "1";

  @Test
  public void getCrackDataByDeviceIdWithTimeSpan_Returns_ResultSet() throws Exception {
    LocalDateTime startDate = LocalDateTime.of(2019, 07, 1, 9, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2019, 07, 1, 9, 05, 00);
    String deviceId = "Crk01";
    List<IotCrackSensorData> dataList = new ArrayList<>();
    IotCrackSensorData sensorData = mockCrackSensorData();
    dataList.add(sensorData);

    when(crackdisplacementDataRepository.getCrackdisplacementDataByDeviceId(startDate, endDate,
        deviceId)).thenReturn(dataList);

    IotSensorDto result =
        iotSensorDataService.getCrackDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate);

    assertNotNull(result);
    assertEquals(result.getDeviceId(), deviceId);
    assertNotNull(result.getDeviceType());
    assertFalse(result.getSensorData().isEmpty());
    assertFalse(result.getSensorData().get(0).getData().isEmpty());
    assertNotNull(result.getSensorData().get(0).getTitle());

    verify(crackdisplacementDataRepository, times(1))
        .getCrackdisplacementDataByDeviceId(startDate, endDate, deviceId);
    verifyNoMoreInteractions(crackdisplacementDataRepository);
  }

  @Test
  public void getCrackDataByDeviceIdWithTimeSpan_Returns_EmptyResultSet() throws Exception {
    LocalDateTime startDate = LocalDateTime.of(2019, 07, 1, 9, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2019, 07, 1, 9, 05, 00);
    String deviceId = "Crk01";
    List<IotCrackSensorData> dataList = new ArrayList<>();

    when(crackdisplacementDataRepository.getCrackdisplacementDataByDeviceId(startDate, endDate,
        deviceId)).thenReturn(dataList);

    IotSensorDto result =
        iotSensorDataService.getCrackDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate);

    assertNotNull(result);
    assertEquals(result.getDeviceId(), deviceId);
    assertNull(result.getDeviceType());
    assertTrue(result.getSensorData().get(0).getData().isEmpty());
    assertNotNull(result.getSensorData().get(0).getTitle());

    verify(crackdisplacementDataRepository, times(1))
        .getCrackdisplacementDataByDeviceId(startDate, endDate, deviceId);
    verifyNoMoreInteractions(crackdisplacementDataRepository);
  }

  @Test
  public void getInclinometerDataByDeviceIdWithTimeSpan_Returns_ResultSet() throws Exception {
    LocalDateTime startDate = LocalDateTime.of(2019, 07, 1, 9, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2019, 07, 1, 9, 05, 00);
    String deviceId = "Inklinometer01";
    List<IotInclinometerData> dataList = new ArrayList<>();
    IotInclinometerData sensorData = mockInclinometerData();
    dataList.add(sensorData);

    when(inclinometerDataRepository.getInclinometerDataWithTimeSpan(Mockito.matches(deviceId),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(dataList);

    IotSensorDto result = iotSensorDataService.getInclinometerDataByDeviceIdWithTimeSpan(deviceId,
        startDate, endDate);

    assertNotNull(result);
    assertEquals(result.getDeviceId(), deviceId);
    assertNotNull(result.getDeviceType());
    assertFalse(result.getSensorData().isEmpty());
    assertFalse(result.getSensorData().get(0).getData().isEmpty());
    assertNotNull(result.getSensorData().get(0).getTitle());
    assertEquals(4, result.getSensorData().size());

    verify(inclinometerDataRepository, times(1)).getInclinometerDataWithTimeSpan(
        Mockito.matches(deviceId), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class));
    verifyNoMoreInteractions(inclinometerDataRepository);
  }

  @Test
  public void getInclinometerDataByDeviceIdWithTimeSpan_Returns_EmptyResultSet() throws Exception {
    LocalDateTime startDate = LocalDateTime.of(2019, 07, 1, 9, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2019, 07, 1, 9, 05, 00);
    String deviceId = "Inklinometer01";
    List<IotInclinometerData> dataList = new ArrayList<>();

    when(inclinometerDataRepository.getInclinometerDataWithTimeSpan(Mockito.matches(deviceId),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(dataList);

    IotSensorDto result = iotSensorDataService.getInclinometerDataByDeviceIdWithTimeSpan(deviceId,
        startDate, endDate);

    assertNotNull(result);
    assertEquals(result.getDeviceId(), deviceId);
    assertNull(result.getDeviceType());
    assertTrue(result.getSensorData().get(0).getData().isEmpty());
    assertNotNull(result.getSensorData().get(0).getTitle());

    verify(inclinometerDataRepository, times(1)).getInclinometerDataWithTimeSpan(
        Mockito.matches(deviceId), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class));
    verifyNoMoreInteractions(inclinometerDataRepository);
  }

  @Test
  public void getAccelerometerDataByDeviceIdWithTimeSpan_Returns_ResultSet() throws Exception {
    LocalDateTime startDate = LocalDateTime.of(2019, 07, 1, 9, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2019, 07, 1, 9, 05, 00);
    String deviceId = "accelerometerffm";
    List<AccelerometerData> dataList = new ArrayList<>();
    AccelerometerData sensorData = mockAccelerometerData();
    dataList.add(sensorData);

    when(accelerometerDataRepository.getAccelerometerDataByDeviceIdWithTimeSpan(deviceId, startDate,
        endDate)).thenReturn(dataList);

    IotSensorDto result = iotSensorDataService.getAccelerometerDataByDeviceIdWithTimeSpan(deviceId,
        startDate, endDate);

    assertNotNull(result);
    assertEquals(result.getDeviceId(), deviceId);
    assertNotNull(result.getDeviceType());
    assertFalse(result.getSensorData().isEmpty());
    assertFalse(result.getSensorData().get(0).getData().isEmpty());
    assertNotNull(result.getSensorData().get(0).getTitle());

    verify(accelerometerDataRepository, times(1))
        .getAccelerometerDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate);
    verifyNoMoreInteractions(accelerometerDataRepository);
  }

  @Test
  public void getAccelerometerDataByDeviceIdWithTimeSpan_Returns_EmptyResultSet() throws Exception {
    LocalDateTime startDate = LocalDateTime.of(2019, 07, 1, 9, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2019, 07, 1, 9, 05, 00);
    String deviceId = "accelerometerffm";
    List<AccelerometerData> dataList = new ArrayList<>();

    when(accelerometerDataRepository.getAccelerometerDataByDeviceIdWithTimeSpan(deviceId, startDate,
        endDate)).thenReturn(dataList);

    IotSensorDto result = iotSensorDataService.getAccelerometerDataByDeviceIdWithTimeSpan(deviceId,
        startDate, endDate);

    assertNotNull(result);
    assertEquals(result.getDeviceId(), deviceId);
    assertNull(result.getDeviceType());
    assertTrue(result.getSensorData().get(0).getData().isEmpty());
    assertNotNull(result.getSensorData().get(0).getTitle());

    verify(accelerometerDataRepository, times(1))
        .getAccelerometerDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate);
    verifyNoMoreInteractions(accelerometerDataRepository);
  }

  @Test
  public void getImuDataByDeviceIdWithTimeSpan_Returns_ResultSet() throws Exception {
    LocalDateTime startDate = LocalDateTime.of(2019, 07, 1, 9, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2019, 07, 1, 9, 05, 00);
    String deviceId = "IMU01";
    List<ImuData> dataList = new ArrayList<>();
    ImuData sensorData = mockImuData();
    dataList.add(sensorData);

    when(imuDataRepository.getImuDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate))
        .thenReturn(dataList);

    IotSensorDto result =
        iotSensorDataService.getImuDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate);

    assertNotNull(result);
    assertEquals(result.getDeviceId(), deviceId);
    assertNotNull(result.getDeviceType());
    assertFalse(result.getSensorData().isEmpty());
    assertFalse(result.getSensorData().get(0).getData().isEmpty());
    assertNotNull(result.getSensorData().get(0).getTitle());

    verify(imuDataRepository, times(1)).getImuDataByDeviceIdWithTimeSpan(deviceId,
        startDate, endDate);
    verifyNoMoreInteractions(imuDataRepository);
  }

  @Test
  public void getImuDataByDeviceIdWithTimeSpan_Returns_EmptyResultSet() throws Exception {
    LocalDateTime startDate = LocalDateTime.of(2019, 07, 1, 9, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2019, 07, 1, 9, 05, 00);
    String deviceId = "IMU01";
    List<ImuData> dataList = new ArrayList<>();

    when(imuDataRepository.getImuDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate))
        .thenReturn(dataList);

    IotSensorDto result =
        iotSensorDataService.getImuDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate);

    assertNotNull(result);
    assertEquals(result.getDeviceId(), deviceId);
    assertNull(result.getDeviceType());
    assertTrue(result.getSensorData().get(0).getData().isEmpty());
    assertNotNull(result.getSensorData().get(0).getTitle());

    verify(imuDataRepository, times(1)).getImuDataByDeviceIdWithTimeSpan(deviceId,
        startDate, endDate);
    verifyNoMoreInteractions(imuDataRepository);
  }

  @Test
  public void getTemperatureDataByDeviceIdWithTimeSpan_Returns_ResultSet() throws Exception {
    LocalDateTime startDate = LocalDateTime.of(2019, 8, 8, 8, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2019, 8, 8, 9, 00, 00);
    String deviceId = "Temperature01";
    List<TemperatureData> sensorDataList = mockTempData();

    when(temperatureDataRepository.getTemperatureDataByDeviceIdWithTimeSpan(deviceId, startDate,
        endDate)).thenReturn(sensorDataList);

    IotSensorDto result =
        iotSensorDataService.getTemperatureDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate);

    assertNotNull(result);
    assertEquals(result.getDeviceId(), deviceId);
    assertNotNull(result.getDeviceType());
    assertFalse(result.getSensorData().isEmpty());
    assertFalse(result.getSensorData().get(0).getData().isEmpty());
    assertNotNull(result.getSensorData().get(0).getTitle());

    verify(temperatureDataRepository, times(1))
        .getTemperatureDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate);
    verifyNoMoreInteractions(temperatureDataRepository);
  }

  @Test
  public void getTemperatureDataByDeviceIdWithTimeSpan_Returns_EmptyResultSet() throws Exception {
    LocalDateTime startDate = LocalDateTime.of(2019, 8, 8, 8, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2019, 8, 8, 9, 00, 00);
    String deviceId = "Temperature01";
    List<TemperatureData> sensorDataList = new ArrayList<>();

    when(temperatureDataRepository.getTemperatureDataByDeviceIdWithTimeSpan(deviceId, startDate,
        endDate)).thenReturn(sensorDataList);

    IotSensorDto result =
        iotSensorDataService.getTemperatureDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate);

    assertNotNull(result);
    assertEquals(result.getDeviceId(), deviceId);
    assertNull(result.getDeviceType());
    assertTrue(result.getSensorData().get(0).getData().isEmpty());
    assertNotNull(result.getSensorData().get(0).getTitle());

    verify(temperatureDataRepository, times(1))
        .getTemperatureDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate);
    verifyNoMoreInteractions(temperatureDataRepository);
  }

  @Test
  public void getCrackSensorDataByDeviceId_Maxview_Returns_EmptyResultSet() throws Exception {
    List<IotCrackSensorData> dataList = new ArrayList<>();

    LocalDateTime endDate = LocalDateTime.now().minusHours(2);
    LocalDateTime startDate = endDate.minusMinutes(15);
    String size = "maxview";
    String deviceId = "Crk01";

    when(crackdisplacementDataRepository.getDataOfLatestEntryByDevice(deviceId))
        .thenReturn(endDate);

    when(crackdisplacementDataRepository.getCrackdisplacementDataByDeviceId(startDate, endDate,
        deviceId)).thenReturn(dataList);

    List<SensorDataDto> result = iotSensorDataService.getCrackSensorDataByDeviceId(deviceId, size);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertNotNull(result.get(0));
    assertEquals(0, result.get(0).getData().size());

    verify(crackdisplacementDataRepository, times(1))
        .getCrackdisplacementDataByDeviceId(startDate, endDate, deviceId);
    verify(crackdisplacementDataRepository, times(1))
        .getDataOfLatestEntryByDevice(deviceId);
    verifyNoMoreInteractions(crackdisplacementDataRepository);
  }

  @Test
  public void getCrackSensorDataByDeviceId_Maxview_Returns_Null() throws Exception {
    String size = "maxview";
    String deviceId = "Crk01";

    when(crackdisplacementDataRepository.getDataOfLatestEntryByDevice(deviceId)).thenReturn(null);

    List<SensorDataDto> result = iotSensorDataService.getCrackSensorDataByDeviceId(deviceId, size);

    assertNull(result);

    verify(crackdisplacementDataRepository, times(1))
        .getDataOfLatestEntryByDevice(deviceId);
    verifyNoMoreInteractions(crackdisplacementDataRepository);
  }

  @Test
  public void getCrackSensorDataByDeviceId_Minview_Returns_ResultSet() throws Exception {
    IotCrackSensorData sensorData = mockCrackSensorData();
    List<IotCrackSensorData> dataList = new ArrayList<>();
    dataList.add(sensorData);

    LocalDateTime endDate = LocalDateTime.now().minusHours(2);
    LocalDateTime startDate = endDate.minusMinutes(5);
    String size = "minview";
    String deviceId = "Crk01";

    when(crackdisplacementDataRepository.getDataOfLatestEntryByDevice(deviceId))
        .thenReturn(endDate);

    when(crackdisplacementDataRepository.getCrackdisplacementDataByDeviceId(
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class),
        Mockito.matches(deviceId))).thenReturn(dataList);

    List<SensorDataDto> result = iotSensorDataService.getCrackSensorDataByDeviceId(deviceId, size);

    assertNotNull(result);
    assertEquals(endDate.minusMinutes(5), startDate);

    verify(crackdisplacementDataRepository, times(1)).getCrackdisplacementDataByDeviceId(
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class),
        Mockito.matches(deviceId));
    verify(crackdisplacementDataRepository, times(1))
        .getDataOfLatestEntryByDevice(deviceId);
    verifyNoMoreInteractions(crackdisplacementDataRepository);
  }

  @Test
  public void getCrackSensorDataByDeviceId_Minview_Returns_EmptyResultSet() throws Exception {
    List<IotCrackSensorData> dataList = new ArrayList<>();

    LocalDateTime endDate = LocalDateTime.now().minusHours(2);
    LocalDateTime startDate = endDate.minusMinutes(5);
    String size = "minview";
    String deviceId = "Crk01";

    when(crackdisplacementDataRepository.getDataOfLatestEntryByDevice(deviceId))
        .thenReturn(endDate);

    when(crackdisplacementDataRepository.getCrackdisplacementDataByDeviceId(startDate, endDate,
        deviceId)).thenReturn(dataList);

    List<SensorDataDto> result = iotSensorDataService.getCrackSensorDataByDeviceId(deviceId, size);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertNotNull(result.get(0));
    assertEquals(0, result.get(0).getData().size());

    verify(crackdisplacementDataRepository, times(1))
        .getCrackdisplacementDataByDeviceId(startDate, endDate, deviceId);
    verify(crackdisplacementDataRepository, times(1))
        .getDataOfLatestEntryByDevice(deviceId);
    verifyNoMoreInteractions(crackdisplacementDataRepository);
  }

  @Test
  public void getInclinometerDataByDeviceId_Minview_Returns_ResultSet() throws Exception {
    IotInclinometerData sensorData = mockInclinometerData();
    List<IotInclinometerData> dataList = new ArrayList<>();
    dataList.add(sensorData);

    LocalDateTime endDate = LocalDateTime.now().minusHours(2);
    LocalDateTime startDate = endDate.minusMinutes(5);
    String deviceId = "Inklinometer01";

    when(inclinometerDataRepository.getDateOfLatestEntryByDevice(deviceId))
        .thenReturn(endDate);

    when(inclinometerDataRepository.getInclinometerDataWithTimeSpan(Mockito.matches(deviceId),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(dataList);

    List<SensorDataDto> result =
        iotSensorDataService.getInclinometerDataByDeviceIdMinview(deviceId);

    assertNotNull(result);
    assertEquals(endDate.minusMinutes(5), startDate);

    verify(inclinometerDataRepository, times(1))
        .getDateOfLatestEntryByDevice(deviceId);
    verify(inclinometerDataRepository, times(1)).getInclinometerDataWithTimeSpan(
        Mockito.matches(deviceId), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class));
    verifyNoMoreInteractions(inclinometerDataRepository);
  }

  @Test
  public void getInclinometerDataByDeviceId_Minview_Returns_NullResultSet() throws Exception {
    String deviceId = "Inklinometer01";

    when(inclinometerDataRepository.getDateOfLatestEntryByDevice(deviceId))
        .thenReturn(null);

    List<SensorDataDto> result =
        iotSensorDataService.getInclinometerDataByDeviceIdMinview(deviceId);

    assertNull(result);

    verify(inclinometerDataRepository, times(1))
        .getDateOfLatestEntryByDevice(deviceId);
    verifyNoMoreInteractions(inclinometerDataRepository);
  }

  @Test
  public void getAccelerometerDataByDeviceId_Maxview_Returns_ResultSet() throws Exception {
    AccelerometerData sensorData = mockAccelerometerData();
    List<AccelerometerData> dataList = new ArrayList<>();
    dataList.add(sensorData);

    LocalDateTime endDate = LocalDateTime.now().minusHours(2);
    LocalDateTime startDate = endDate.minusMinutes(15);
    String size = "maxview";
    String deviceId = "accelerometerffm";

    when(accelerometerDataRepository.getDateOfLatestEntryByDevice(deviceId)).thenReturn(endDate);

    when(accelerometerDataRepository.getAccelerometerDataByDeviceIdWithTimeSpan(eq(deviceId),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(dataList);

    List<SensorDataDto> result =
        iotSensorDataService.getAccelerometerDataByDeviceId(deviceId, size);

    assertNotNull(result);
    assertEquals(endDate.minusMinutes(15), startDate);

    verify(accelerometerDataRepository, times(1))
        .getAccelerometerDataByDeviceIdWithTimeSpan(eq(deviceId), Mockito.any(LocalDateTime.class),
            Mockito.any(LocalDateTime.class));
    verify(accelerometerDataRepository, times(1)).getDateOfLatestEntryByDevice(deviceId);
    verifyNoMoreInteractions(accelerometerDataRepository);
  }

  @Test
  public void getAccelerometerDataByDeviceId_Maxview_Returns_Null() throws Exception {
    String size = "maxview";
    String deviceId = "accelerometerffm";

    when(accelerometerDataRepository.getDateOfLatestEntryByDevice(deviceId)).thenReturn(null);

    List<SensorDataDto> result =
        iotSensorDataService.getAccelerometerDataByDeviceId(deviceId, size);

    assertNull(result);

    verify(accelerometerDataRepository, times(1)).getDateOfLatestEntryByDevice(deviceId);
    verifyNoMoreInteractions(accelerometerDataRepository);
  }


  private List<IotInclinometerData> createInclinometerData(String deviceId, DeviceType deviceType) {
    List<IotInclinometerData> result = new ArrayList<>();

    IotInclinometerData data = new IotInclinometerData();
    data.setDeviceId(deviceId);
    data.setDeviceType(deviceType.getLabel());

    data.setPitchValue((float) random());
    data.setRollValue((float) random());
    data.setPitchMavg((float) random());
    data.setRollMavg((float) random());
    data.setMeasuredAt(LocalDateTime.now().minusMinutes(10));

    result.add(data);

    return result;
  }


  @Test
  public void getDeviceConfiguration_setsAlarmLevelStatus() throws Exception {
    // Given:
    LocalDateTime endDate = LocalDateTime.now().minusHours(2);
    LocalDateTime startDate = endDate.minusMinutes(5);
    String size = null;
    DeviceType expectedDeviceType = DeviceType.INCLINOMETER;

    DeviceDataDto expectedDeviceDataDto = new DeviceDataDto();
    expectedDeviceDataDto.setDeviceId(deviceId);
    expectedDeviceDataDto.setDeviceType(expectedDeviceType.getLabel());
    List<SensorDataDto> expectedDataseries = mockDataSeries(IotInclinometerData.Dataseries.values());
    expectedDeviceDataDto.setDataseries(expectedDataseries);
    when(deviceManagementService.getDeviceConfiguration(expectedDeviceType, deviceId))
    .thenReturn(expectedDeviceDataDto);

    List<IotInclinometerData> expectedInclinometerData = createInclinometerData(deviceId, expectedDeviceType);
    when(inclinometerDataRepository.getDataByDeviceIdAndMeasuredAt(eq(deviceId), any()))
    .thenReturn(expectedInclinometerData);

    AlertLevel expectedAlertLevel = AlertLevel.CRITICAL;
    when(iotPlatformGateway.getAlertLevel(expectedDeviceType, deviceId)).thenReturn(expectedAlertLevel);


    // When:
    DeviceDataDto actualDeviceConfig = iotSensorDataService.getDeviceConfiguration(expectedDeviceType, deviceId,
        startDate, endDate, size);

    // Then:
    assertThat(actualDeviceConfig.getAlertLevelStatus()).isEqualTo(expectedAlertLevel);
  }


  @Test
  public void getDeviceConfiguration_withStartAndEndDate_dontOverrideDeviceManagementService() throws Exception {
    // Given:
    LocalDateTime endDate = LocalDateTime.now().minusHours(2);
    LocalDateTime startDate = endDate.minusMinutes(5);
    String size = null;
    DeviceType expectedDeviceType = DeviceType.INCLINOMETER;

    DeviceDataDto expectedDeviceDataDto = new DeviceDataDto();
    expectedDeviceDataDto.setDeviceId(deviceId);
    expectedDeviceDataDto.setDeviceType(expectedDeviceType.getLabel());
    List<SensorDataDto> expectedDataseries = mockDataSeries(IotInclinometerData.Dataseries.values());
    expectedDeviceDataDto.setDataseries(expectedDataseries);
    when(deviceManagementService.getDeviceConfiguration(expectedDeviceType, deviceId))
    .thenReturn(expectedDeviceDataDto);

    List<IotInclinometerData> expectedInclinometerData = createInclinometerData(deviceId, expectedDeviceType);
    when(inclinometerDataRepository.getDataByDeviceIdAndMeasuredAt(eq(deviceId), any()))
    .thenReturn(expectedInclinometerData);

    when(inclinometerDataRepository.getInclinometerDataWithTimeSpan(eq(deviceId), any(), any()))
    .thenReturn(expectedInclinometerData);


    // When:
    DeviceDataDto actualDeviceConfig = iotSensorDataService.getDeviceConfiguration(expectedDeviceType, deviceId,
        startDate, endDate, size);

    // Then:
    assertThat(actualDeviceConfig.getDataseries()).allSatisfy(ds -> {
      assertThat(ds.getData()).isNotEmpty();
      SensorDataDto foundSensorData = expectedDataseries.stream().filter(item -> item.getId().equalsIgnoreCase(ds.getId())).findAny().get();
      assertThat(ds.getTitle()).isEqualTo(foundSensorData.getTitle());
      assertThat(ds.getUnitOfMeasurement()).isEqualTo(foundSensorData.getUnitOfMeasurement());
      assertThat(ds.getData()).containsExactlyElementsOf(foundSensorData.getData());
    });
  }


  @Test
  public void getDeviceConfigurationCrk_Returns_ResultSet() throws Exception {
    LocalDateTime endDate = LocalDateTime.now().minusHours(2);
    LocalDateTime startDate = endDate.minusMinutes(5);
    String size = "minview";
    String deviceId = "Crk01";
    DeviceType deviceTypeCrk = DeviceType.CRACK;
    DeviceDataDto deviceData = new DeviceDataDto();
    deviceData.setDeviceType(deviceTypeCrk.getLabel());
    deviceData.setDeviceId(deviceId);
    deviceData.setDataseries(mockDataSeries(AbstractCrackSensorData.Dataseries.values()));
    List<IotCrackSensorData> dataList = new ArrayList<>();
    dataList.add(mockCrackSensorData());

    when(deviceManagementService.getDeviceConfiguration(deviceTypeCrk, deviceId))
        .thenReturn(deviceData);

    when(crackdisplacementDataRepository.getCrackdisplacementDataByDeviceId(startDate, endDate,
        deviceId)).thenReturn(dataList);

    when(crackdisplacementDataRepository.getDataByDeviceIdAndMeasuredAt(eq(deviceId), any()))
    .thenReturn(dataList);

    when(crackdisplacementDataRepository.getDataOfLatestEntryByDevice(deviceId))
        .thenReturn(endDate);

    DeviceDataDto result = iotSensorDataService.getDeviceConfiguration(deviceTypeCrk, deviceId,
        startDate, endDate, size);

    assertNotNull(result);
    assertThat(deviceTypeCrk).toString().equals(result.getDeviceType());
    assertEquals(deviceId, result.getDeviceId());

    verify(deviceManagementService, times(1)).getDeviceConfiguration(deviceTypeCrk,
        deviceId);
    verify(crackdisplacementDataRepository, times(1))
        .getCrackdisplacementDataByDeviceId(startDate, endDate, deviceId);
    verify(crackdisplacementDataRepository, times(1)).getCrackdisplacementDataByDeviceId(
        startDate.minusMinutes(DIFF_MINUTES_LOCAL_TO_UTC),
        endDate.minusMinutes(DIFF_MINUTES_LOCAL_TO_UTC), deviceId);
    verify(crackdisplacementDataRepository, times(2))
        .getDataOfLatestEntryByDevice(deviceId);
    verify(crackdisplacementDataRepository, times(2))
    .getDataByDeviceIdAndMeasuredAt(eq(deviceId), any());
    verifyNoMoreInteractions(deviceManagementService, crackdisplacementDataRepository,
        imuDataRepository, inclinometerDataRepository, accelerometerDataRepository, logger);
  }

  @Test
  public void getDeviceConfigurationCrkNoDataseries_Returns_ResultSet() throws Exception {
    String deviceId = "Crk01";
    DeviceType deviceTypeCrk = DeviceType.CRACK;
    DeviceDataDto deviceData = new DeviceDataDto();
    deviceData.setDeviceType(deviceTypeCrk.getLabel());
    deviceData.setDeviceId(deviceId);

    when(deviceManagementService.getDeviceConfiguration(deviceTypeCrk, deviceId))
    .thenReturn(deviceData);

    DeviceDataDto result =
        iotSensorDataService.getDeviceConfiguration(deviceTypeCrk, deviceId, null, null, null);

    assertNotNull(result);
    assertThat(deviceTypeCrk).toString().equals(result.getDeviceId());
    assertEquals(deviceId, result.getDeviceId());

    verify(deviceManagementService, times(1)).getDeviceConfiguration(deviceTypeCrk,
        deviceId);
    verify(crackdisplacementDataRepository).getDataOfLatestEntryByDevice(deviceId);
    verifyNoMoreInteractions(deviceManagementService, crackdisplacementDataRepository,
        imuDataRepository, inclinometerDataRepository, accelerometerDataRepository, logger);
  }

  @Test
  public void getDeviceConfigurationImu_Returns_ResultSet() throws Exception {
    LocalDateTime endDate = LocalDateTime.now().minusHours(2);
    LocalDateTime startDate = endDate.minusMinutes(5);
    String size = "minview";
    String deviceId = "IMU01";
    DeviceType deviceTypeImu = DeviceType.IMU;
    DeviceDataDto deviceData = new DeviceDataDto();
    deviceData.setDeviceType(deviceTypeImu.getLabel());
    deviceData.setDeviceId(deviceId);
    deviceData.setDataseries(mockDataSeries(ImuData.Dataseries.values()));
    List<ImuData> dataList = new ArrayList<>();
    dataList.add(mockImuData());

    when(deviceManagementService.getDeviceConfiguration(deviceTypeImu, deviceId))
        .thenReturn(deviceData);

    when(imuDataRepository.getImuDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate))
        .thenReturn(dataList);

    when(imuDataRepository.getDataByDeviceIdAndMeasuredAt(eq(deviceId), any()))
    .thenReturn(dataList);

    DeviceDataDto result = iotSensorDataService.getDeviceConfiguration(deviceTypeImu, deviceId,
        startDate, endDate, size);

    assertNotNull(result);
    assertThat(deviceTypeImu).toString().equals(result.getDeviceType());
    assertEquals(deviceId, result.getDeviceId());

    verify(deviceManagementService, times(1)).getDeviceConfiguration(deviceTypeImu,
        deviceId);
    verify(imuDataRepository, times(1)).getImuDataByDeviceIdWithTimeSpan(deviceId,
        startDate.minusMinutes(DIFF_MINUTES_LOCAL_TO_UTC),
        endDate.minusMinutes(DIFF_MINUTES_LOCAL_TO_UTC));
    verify(imuDataRepository).getDateOfLatestEntryByDevice(deviceId);
    verify(imuDataRepository, times(2)).getDataByDeviceIdAndMeasuredAt(eq(deviceId), any());
    verify(logger, times(1)).debug(anyString(), eq(DeviceType.IMU));
    verifyNoMoreInteractions(deviceManagementService, crackdisplacementDataRepository,
        imuDataRepository, inclinometerDataRepository, accelerometerDataRepository, logger);
  }

  @Test
  public void getDeviceConfigurationInc_Returns_ResultSet() throws Exception {
    LocalDateTime endDate = LocalDateTime.now().minusHours(2);
    LocalDateTime startDate = endDate.minusMinutes(5);
    String deviceId = "Inklinometer01";
    DeviceType deviceTypeInc = DeviceType.INCLINOMETER;
    DeviceDataDto deviceData = new DeviceDataDto();
    deviceData.setDeviceType(deviceTypeInc.getLabel());
    deviceData.setDeviceId(deviceId);
    deviceData.setDataseries(mockDataSeries(IotInclinometerData.Dataseries.values()));
    List<IotInclinometerData> dataList = new ArrayList<>();
    dataList.add(mockInclinometerData());

    when(deviceManagementService.getDeviceConfiguration(deviceTypeInc, deviceId))
        .thenReturn(deviceData);

    when(inclinometerDataRepository.getInclinometerDataWithTimeSpan(deviceId, startDate, endDate))
        .thenReturn(dataList);

    when(inclinometerDataRepository.getDataByDeviceIdAndMeasuredAt(eq(deviceId), any()))
    .thenReturn(dataList);

    DeviceDataDto result = iotSensorDataService.getDeviceConfiguration(deviceTypeInc, deviceId,
        startDate, endDate, null);

    assertNotNull(result);
    assertThat(deviceTypeInc).toString().equals(result.getDeviceType());
    assertEquals(deviceId, result.getDeviceId());

    verify(deviceManagementService, times(1)).getDeviceConfiguration(deviceTypeInc,
        deviceId);
    verify(inclinometerDataRepository, times(1)).getInclinometerDataWithTimeSpan(deviceId,
        startDate.minusMinutes(DIFF_MINUTES_LOCAL_TO_UTC),
        endDate.minusMinutes(DIFF_MINUTES_LOCAL_TO_UTC));
    verify(inclinometerDataRepository, times(2)).getDataByDeviceIdAndMeasuredAt(eq(deviceId), any());
    verify(inclinometerDataRepository).getDateOfLatestEntryByDevice(deviceId);
    verifyNoMoreInteractions(deviceManagementService, crackdisplacementDataRepository,
        imuDataRepository, inclinometerDataRepository, accelerometerDataRepository, logger);
  }

  @Test
  public void getDeviceConfigurationAcc_Returns_ResultSet() throws Exception {
    LocalDateTime endDate = LocalDateTime.now().minusHours(2);
    LocalDateTime startDate = endDate.minusMinutes(5);
    String size = "minview";
    String deviceId = "accelerometerffm";
    DeviceType deviceTypeAcc = DeviceType.ACCELEROMETER;
    DeviceDataDto deviceData = new DeviceDataDto();
    deviceData.setDeviceType(deviceTypeAcc.getLabel());
    deviceData.setDeviceId(deviceId);
    deviceData.setDataseries(mockDataSeries(AccelerometerData.Dataseries.values()));
    List<AccelerometerData> dataList = new ArrayList<>();
    dataList.add(mockAccelerometerData());

    when(deviceManagementService.getDeviceConfiguration(deviceTypeAcc, deviceId))
        .thenReturn(deviceData);

    when(accelerometerDataRepository.getAccelerometerDataByDeviceIdWithTimeSpan(deviceId, startDate,
        endDate)).thenReturn(dataList);

    when(accelerometerDataRepository.getDataByDeviceIdAndMeasuredAt(eq(deviceId), any())).thenReturn(dataList);

    when(accelerometerDataRepository.getDateOfLatestEntryByDevice(deviceId)).thenReturn(endDate);

    DeviceDataDto result = iotSensorDataService.getDeviceConfiguration(deviceTypeAcc, deviceId,
        startDate, endDate, size);

    assertNotNull(result);
    assertThat(deviceTypeAcc).toString().equals(result.getDeviceType());
    assertEquals(deviceId, result.getDeviceId());

    verify(deviceManagementService, times(1)).getDeviceConfiguration(deviceTypeAcc,
        deviceId);
    verify(accelerometerDataRepository, times(1))
        .getAccelerometerDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate);
    verify(accelerometerDataRepository, times(1))
        .getAccelerometerDataByDeviceIdWithTimeSpan(deviceId,
            startDate.minusMinutes(DIFF_MINUTES_LOCAL_TO_UTC),
            endDate.minusMinutes(DIFF_MINUTES_LOCAL_TO_UTC));
    verify(accelerometerDataRepository, times(2)).getDateOfLatestEntryByDevice(deviceId);
    verify(accelerometerDataRepository, times(2)).getDataByDeviceIdAndMeasuredAt(eq(deviceId), any());
    verifyNoMoreInteractions(deviceManagementService, crackdisplacementDataRepository,
        imuDataRepository, inclinometerDataRepository, accelerometerDataRepository, logger);
  }

  @Test
  public void getDeviceConfigurationCrk_Returns_EmptyResultSet() throws Exception {
    String deviceId = "Crk01";
    DeviceType deviceTypeCrk = DeviceType.CRACK;

    when(deviceManagementService.getDeviceConfiguration(deviceTypeCrk, deviceId)).thenReturn(null);

    DeviceDataDto result =
        iotSensorDataService.getDeviceConfiguration(deviceTypeCrk, deviceId, null, null, null);

    assertNull(result);

    verify(deviceManagementService, times(1)).getDeviceConfiguration(deviceTypeCrk,
        deviceId);
    verifyNoMoreInteractions(deviceManagementService, crackdisplacementDataRepository,
        imuDataRepository, inclinometerDataRepository, accelerometerDataRepository, logger);
  }

  @Test
  public void getDeviceConfigurationTemp_Returns_ResultSet() throws Exception {
    String deviceId = "CHRissMini64_01TLF";
    DeviceType deviceTypeTmp = DeviceType.TEMPERATURE;
    LocalDateTime endDate = LocalDateTime.now().minusHours(2);
    LocalDateTime startDate = endDate.minusMinutes(5);

    DeviceDataDto deviceData = new DeviceDataDto();
    deviceData.setDeviceType(deviceTypeTmp.getLabel());
    deviceData.setDeviceId(deviceId);
    deviceData.setDataseries(mockDataSeries(TemperatureData.Dataseries.values()));

    List<TemperatureData> dataList = mockTempData();

    when(deviceManagementService.getDeviceConfiguration(deviceTypeTmp, deviceId))
      .thenReturn(deviceData);

    when(temperatureDataRepository.getTemperatureDataByDeviceIdWithTimeSpan(deviceId, startDate, endDate))
      .thenReturn(dataList);

    DeviceDataDto result = iotSensorDataService.getDeviceConfiguration(deviceTypeTmp, deviceId,
        startDate, endDate, null);

    assertNotNull(result);
    assertThat(deviceTypeTmp).toString().equals(result.getDeviceType());
    assertEquals(deviceId, result.getDeviceId());

  }

  @Test
  public void putDeviceConfiguration_Returns_ResultSet() throws Exception {
    String deviceId = "Crk01";
    DeviceType deviceTypeCrk = DeviceType.CRACK;
    String deviceDataString = "{\"sensorType\":\"" + sensorType + "\"}";
    DeviceDataDto deviceData = new DeviceDataDto();
    deviceData.setSensorType(sensorType);
    JsonObject deviceDataJson = new JsonParser().parse(deviceDataString).getAsJsonObject();

    when(deviceManagementService.putDeviceConfiguration(deviceTypeCrk, deviceId, deviceDataJson))
        .thenReturn(deviceData);

    DeviceDataDto result =
        iotSensorDataService.putDeviceConfiguration(deviceTypeCrk, deviceId, deviceDataString);

    assertNotNull(result);
    assertEquals(sensorType, result.getSensorType());

    verify(deviceManagementService, times(1)).putDeviceConfiguration(deviceTypeCrk,
        deviceId, deviceDataJson);
    verify(domainEventPublisher, times(1)).publishRabbitmqEvent(Mockito.anyString());
    verifyNoMoreInteractions(deviceManagementService, domainEventPublisher, logger);
  }

  @Test
  public void putDeviceConfiguration_Returns_PublishError() throws Exception {
    String deviceId = "Crk01";
    DeviceType deviceTypeCrk = DeviceType.CRACK;
    String deviceDataString = "{\"sensorType\":\"" + sensorType + "\"}";
    DeviceDataDto deviceData = new DeviceDataDto();
    deviceData.setSensorType(sensorType);
    JsonObject deviceDataJson = new JsonParser().parse(deviceDataString).getAsJsonObject();

    when(deviceManagementService.putDeviceConfiguration(deviceTypeCrk, deviceId, deviceDataJson))
        .thenReturn(deviceData);

    doThrow(new AmqpException("pebcak")).when(domainEventPublisher)
        .publishRabbitmqEvent(Mockito.anyString());

    DeviceDataDto result =
        iotSensorDataService.putDeviceConfiguration(deviceTypeCrk, deviceId, deviceDataString);

    assertNotNull(result);
    assertEquals(sensorType, result.getSensorType());

    verify(deviceManagementService, times(1)).putDeviceConfiguration(deviceTypeCrk,
        deviceId, deviceDataJson);
    verify(domainEventPublisher, times(1)).publishRabbitmqEvent(Mockito.anyString());
    verify(logger, times(1)).error(Mockito.anyString(), Mockito.anyString());
    verifyNoMoreInteractions(deviceManagementService, domainEventPublisher, logger);
  }

  @Test
  public void putDeviceConfiguration_Returns_EmptyResultSet() throws Exception {
    String deviceId = "Crk01";
    DeviceType deviceTypeCrk = DeviceType.CRACK;
    String deviceDataString = "{\"sensorType\":\"" + sensorType + "\"}";
    JsonObject deviceDataJson = new JsonParser().parse(deviceDataString).getAsJsonObject();

    when(deviceManagementService.putDeviceConfiguration(deviceTypeCrk, deviceId, deviceDataJson))
        .thenReturn(null);

    DeviceDataDto result =
        iotSensorDataService.putDeviceConfiguration(deviceTypeCrk, deviceId, deviceDataString);

    assertNull(result);

    verify(deviceManagementService, times(1)).putDeviceConfiguration(deviceTypeCrk,
        deviceId, deviceDataJson);
    verifyNoMoreInteractions(deviceManagementService, domainEventPublisher, logger);
  }

  @Test
  public void getDeviceDataForGraphAcc_Returns_ResultSet() throws Exception {
    String deviceId = "acceleromerffm";
    DeviceType deviceTypeAcc = DeviceType.ACCELEROMETER;
    LocalDateTime endDate = LocalDateTime.now();
    LocalDateTime startDate = endDate.minusMinutes(5);
    String size = "minview";

    DeviceDataGraphDto result = iotSensorDataService.getDeviceDataForGraph(deviceTypeAcc, deviceId,
        startDate, endDate, size);

    assertNotNull(result);
    assertEquals(deviceId, result.getDeviceId());
    assertNotNull(result.getSensorData());
  }

  @Test
  public void getDeviceDataForGraphInc_Returns_ResultSet() throws Exception {
    String deviceId = "Inklinometer01";
    LocalDateTime endDate = LocalDateTime.now();
    LocalDateTime startDate = endDate.minusMinutes(5);
    String size = "minview";
    DeviceType deviceTypeInc = DeviceType.INCLINOMETER;

    DeviceDataGraphDto result = iotSensorDataService.getDeviceDataForGraph(deviceTypeInc, deviceId,
        startDate, endDate, size);

    assertNotNull(result);
    assertEquals(deviceId, result.getDeviceId());
    assertNotNull(result.getSensorData());
  }

  @Test
  public void getDeviceDataForGraphCrk_Returns_ResultSet() throws Exception {
    String deviceId = "Crk01";
    DeviceType deviceTypeCrk = DeviceType.CRACK;
    LocalDateTime endDate = LocalDateTime.now();
    LocalDateTime startDate = endDate.minusMinutes(5);
    String size = "minview";

    DeviceDataGraphDto result = iotSensorDataService.getDeviceDataForGraph(deviceTypeCrk, deviceId,
        startDate, endDate, size);

    assertNotNull(result);
    assertEquals(deviceId, result.getDeviceId());
    assertNotNull(result.getSensorData());
  }

  @Test
  public void getDeviceDataForGraphCrk_NoSizeNoTimeReturns_ResultSet() throws Exception {
    IotCrackSensorData sensorData = mockCrackSensorData();
    List<IotCrackSensorData> dataList = new ArrayList<>();
    dataList.add(sensorData);
    String deviceId = "Crk01";
    DeviceType deviceTypeCrk = DeviceType.CRACK;
    LocalDateTime endDate = LocalDateTime.now();

    when(crackdisplacementDataRepository.getDataOfLatestEntryByDevice(deviceId))
        .thenReturn(endDate);

    when(crackdisplacementDataRepository.getCrackdisplacementDataByDeviceId(
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class),
        Mockito.matches(deviceId))).thenReturn(dataList);

    DeviceDataGraphDto result =
        iotSensorDataService.getDeviceDataForGraph(deviceTypeCrk, deviceId, null, null, null);

    assertNotNull(result);
    assertEquals(deviceId, result.getDeviceId());
    assertNotNull(result.getGraph());

    verify(crackdisplacementDataRepository, times(1)).getCrackdisplacementDataByDeviceId(
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class),
        Mockito.matches(deviceId));
    verify(crackdisplacementDataRepository, times(1))
        .getDataOfLatestEntryByDevice(deviceId);
    verifyNoMoreInteractions(crackdisplacementDataRepository);
  }

  @Test
  public void getDeviceDataForGraphImu_Returns_ResultSet() throws Exception {
    String deviceId = "IMU01";
    DeviceType deviceTypeImu = DeviceType.IMU;
    LocalDateTime endDate = LocalDateTime.now();
    LocalDateTime startDate = endDate.minusMinutes(5);
    String size = "minview";

    DeviceDataGraphDto result = iotSensorDataService.getDeviceDataForGraph(deviceTypeImu, deviceId,
        startDate, endDate, size);

    assertNotNull(result);
    assertEquals(deviceId, result.getDeviceId());
    assertNotNull(result.getSensorData());
  }

  @Test
  public void getDeviceDataForGraphTemperature_Returns_ResultSet() throws Exception {
    String deviceId = "Temperature01";
    DeviceType deviceTypeTmp = DeviceType.TEMPERATURE;
    LocalDateTime endDate = LocalDateTime.now();
    LocalDateTime startDate = endDate.minusMinutes(5);
    String size = "minview";

    DeviceDataGraphDto result = iotSensorDataService.getDeviceDataForGraph(deviceTypeTmp, deviceId,
        startDate, endDate, size);

    assertNotNull(result);
    assertEquals(deviceId, result.getDeviceId());
    assertNotNull(result.getSensorData());
  }

  @Test
  public void getDeviceDataForGraphInclinometer_Returns_ResultSet() throws Exception {
    String deviceId = "Inklinometer01";
    DeviceType deviceTypeTmp = DeviceType.TEMPERATURE;
    LocalDateTime endDate = LocalDateTime.now();
    LocalDateTime startDate = endDate.minusMinutes(5);
    String size = "minview";

    DeviceDataGraphDto result = iotSensorDataService.getDeviceDataForGraph(deviceTypeTmp, deviceId,
        startDate, endDate, size);

    assertNotNull(result);
    assertEquals(deviceId, result.getDeviceId());
    assertNotNull(result.getSensorData());
  }

  @Test
  public void diffLocalToUtc_Returns_CorrectDateTime() throws Exception {
    LocalDateTime localDate = LocalDateTime.now();
    LocalDateTime utcDate = localDate.minusMinutes(DIFF_MINUTES_LOCAL_TO_UTC);

    LocalDateTime result = iotSensorDataService.diffLocalToUtc(localDate);

    assertNotNull(result);
    assertEquals(utcDate, result);
  }

  private IotCrackSensorData mockCrackSensorData() {
    IotCrackSensorData data = new IotCrackSensorData();
    data.setMeasuredAt(LocalDateTime.of(2018, 06, 20, 23, 00, 00, 000000));
    data.setCrack_value(0.081f);
    data.setDeviceType("Crack Sensor");
    data.setDeviceId("Crk01");

    return data;
  }

  private AccelerometerData mockAccelerometerData() {
    AccelerometerData data = new AccelerometerData();
    data.setDeviceId("accelerometerffm01");
    data.setDeviceType("accelerometer");
    data.setEventType("status");
    data.setFormat("json");
    data.setLogicalInterfaceId("abcdef12345");
    data.setRcvTimestampUtc(LocalDateTime.of(2019, 02, 8, 9, 55, 14));
    data.setMeasuredAt(LocalDateTime.of(2019, 02, 8, 9, 55, 14));
    data.setValue1(0.0123f);
    return data;
  }

  private IotInclinometerData mockInclinometerData() {
    IotInclinometerData data = new IotInclinometerData();
    data.setDeviceId("Inklinometer01");
    data.setDeviceType("Inklinometer");
    data.setRollValue(0.12f);
    data.setPitchValue(-0.1f);
    data.setMeasuredAt(LocalDateTime.now().minusSeconds(30));
    return data;
  }

  private ImuData mockImuData() {
    ImuData data = new ImuData();
    data.setDeviceId("IMU01");
    data.setDeviceType("IMU");
    data.setRoll(0.12f);
    data.setPitch(-0.1f);
    data.setYaw(0.22f);
    data.setRcvTimestampUtc(LocalDateTime.of(2019, 02, 8, 9, 55, 14));
    data.setMeasuredAt(LocalDateTime.of(2019, 02, 8, 9, 55, 14));
    return data;
  }

  private List<TemperatureData> mockTempData() {
    List<TemperatureData> dataList = new ArrayList<>();
    TemperatureData data = new TemperatureData();
    data.setDeviceId("Temperature01");
    data.setDeviceType("Temperature");
    data.setHumidity(43.2f);
    data.setTemperature(19.3f);
    data.setMeasuredAt(LocalDateTime.of(2019, 8, 8, 8, 22, 12));
    dataList.add(data);

    return dataList;
  }

  private List<SensorDataDto> mockDataSeries(BaseDataseries[] dataseries) {
    List<SensorDataDto> result = new ArrayList<>();
    int counter = 1;
    for(BaseDataseries ds : dataseries) {
      result.add(new SensorDataDto(ds.name(), "title" + ds.name(), "unitOfM"+counter, counter, "cv"+counter, (float) (counter/10.0), (float) ((counter+1)/10.0), new ArrayList<>()));
      counter++;
    }
    return result;
  }

}
