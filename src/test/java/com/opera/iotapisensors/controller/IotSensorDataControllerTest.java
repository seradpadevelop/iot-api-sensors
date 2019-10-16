package com.opera.iotapisensors.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.opera.iotapisensors.iotsensor.controller.IotSensorDataController;
import com.opera.iotapisensors.iotsensor.dto.AccelerometerDto;
import com.opera.iotapisensors.iotsensor.dto.CrackSensorDto;
import com.opera.iotapisensors.iotsensor.dto.CrackdisplacementEntitiesToDtoMapper;
import com.opera.iotapisensors.iotsensor.dto.DataDto;
import com.opera.iotapisensors.iotsensor.dto.DeviceDataDto;
import com.opera.iotapisensors.iotsensor.dto.DeviceDataGraphDto;
import com.opera.iotapisensors.iotsensor.dto.HighLowValuesDto;
import com.opera.iotapisensors.iotsensor.dto.SensorDataDto;
import com.opera.iotapisensors.iotsensor.entities.IotCrackSensorData;
import com.opera.iotapisensors.iotsensor.enums.DeviceType;
import com.opera.iotapisensors.iotsensor.repository.CrackdisplacementDataRepository;
import com.opera.iotapisensors.iotsensor.service.IotSensorDataService;
import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = IotSensorDataController.class)
@WebAppConfiguration
@AutoConfigureMockMvc
public class IotSensorDataControllerTest extends TestCase {

  @Autowired
  private MockMvc mvc;

  @Autowired
  WebApplicationContext wac;

  @MockBean
  JwtDecoder decoder;

  @MockBean
  CrackdisplacementDataRepository crackdisplacementDataRepository;

  @MockBean
  IotSensorDataService iotSensorDataService;

  @MockBean
  CrackdisplacementEntitiesToDtoMapper cackdisplacementMapper;

  @InjectMocks
  IotSensorDataController iotSensorDataController;

  ObjectMapper objectMapper = new ObjectMapper();

  Principal principal;

  String projectId = "1";
  String metaDeviceId = "1";
  String userId = "max.mustermann@example.com";
  DeviceType deviceType = DeviceType.CRACK;
  String crackdisplacementDeviceId = "Rissfox Mini RissMini031864";
  String waterlevelDeviceId = "WL-1";
  String accelerometerDeviceId = "accelerometerffm01";
  String sensorType = "Rissfox Mini";
  ArrayList<Object> deviceDataMetrics;
  ArrayList<ArrayList<Object>> sensorData;
  int measurement = 123;

  LocalDateTime date = LocalDateTime.of(2019, 04, 15, 12, 00);
  LocalDateTime startDate = LocalDateTime.of(2019, 03, 11, 11, 00);
  LocalDateTime endDate = LocalDateTime.of(2019, 03, 14, 11, 00);;
  Float tempMesswerte01 = 100.0f;
  Float relfMesswerte01 = 7.5f;
  Float rissMesswerte01 = 35.5f;
  Float u1Messwerte01 = -0.2f;
  Float u2Messwerte01 = 2.34f;
  Float u3Messwerte01 = 0.12f;
  Float u4Messwerte01 = 0.14f;
  LocalDateTime endDateAcc = LocalDateTime.of(2018, 04, 1, 10, 19, 00);
  LocalDateTime startDateAcc = LocalDateTime.of(2018, 04, 1, 10, 18, 00);

  @Before
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(wac).build();

    Jwt mockJwt = mock(Jwt.class);
    Mockito.when(mockJwt.getClaimAsString(Mockito.matches("upn"))).thenReturn(userId);

    principal = new JwtAuthenticationToken(mockJwt);

  }

  @Test
  @WithMockUser
  public void getIotSensorDataByProjectId_Resturns_EmptyResultSet() throws Exception {

    List<IotCrackSensorData> emptyCrackdisplacementDataResultSet = new ArrayList<>();

    when(crackdisplacementDataRepository.getDataOfLatestEntry()).thenReturn(endDate);
    when(crackdisplacementDataRepository.getCrackdisplacementData(startDate, endDate))
        .thenReturn(emptyCrackdisplacementDataResultSet);

    mvc.perform(get("http://localhost:8888/api/sensors/{projectId}", projectId).contextPath("/api")
        .accept(MediaType.APPLICATION_JSON).principal(principal)).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  @WithMockUser
  public void getDeviceConfiguration_Returns_EmptyGraphResultSet() throws Exception {
    String size = "maxview";
    DeviceDataDto deviceData = new DeviceDataDto();
    when(iotSensorDataService.getDeviceConfiguration(deviceType, crackdisplacementDeviceId, null,
        null, size)).thenReturn(deviceData);

    mvc.perform(get(
        "http://localhost:8888/api/sensors/{projectId}/{deviceType}/{crackdisplacementDeviceId}",
        projectId, deviceType, crackdisplacementDeviceId).param("size", size).contextPath("/api")
            .accept(MediaType.APPLICATION_JSON).principal(principal))
        .andExpect(status().isOk());

    verify(iotSensorDataService, times(1)).getDeviceConfiguration(deviceType,
        crackdisplacementDeviceId, null, null, size);
    verifyNoMoreInteractions(iotSensorDataService);
  }

  @Test
  @WithMockUser
  public void getDeviceConfiguration_Returns_DeviceNotFound() throws Exception {
    String size = "maxview";
    when(iotSensorDataService.getDeviceConfiguration(deviceType, crackdisplacementDeviceId, null,
        null, size)).thenReturn(null);

    mvc.perform(get(
        "http://localhost:8888/api/sensors/{projectId}/{deviceType}/{crackdisplacementDeviceId}",
        projectId, deviceType, crackdisplacementDeviceId).param("size", size).contextPath("/api")
            .accept(MediaType.APPLICATION_JSON).principal(principal))
        .andExpect(status().isNotFound());

    verify(iotSensorDataService, times(1)).getDeviceConfiguration(deviceType,
        crackdisplacementDeviceId, null, null, size);
    verifyNoMoreInteractions(iotSensorDataService);
  }

  @Test
  @WithMockUser
  public void getDeviceConfiguration_Returns_BadRequest() throws Exception {
    String size = "maxview";

    mvc.perform(get(
        "http://localhost:8888/api/sensors/{projectId}/{deviceType}/{crackdisplacementDeviceId}",
        projectId, deviceType, crackdisplacementDeviceId).param("size", size)
            .param("endDate", "2019-07-01T21:00:00").contextPath("/api")
            .accept(MediaType.APPLICATION_JSON).principal(principal))
        .andExpect(status().isBadRequest());

    mvc.perform(get(
        "http://localhost:8888/api/sensors/{projectId}/{deviceType}/{crackdisplacementDeviceId}",
        projectId, deviceType, crackdisplacementDeviceId).param("size", size)
            .param("startDate", "2019-07-01T21:02:00").contextPath("/api")
            .accept(MediaType.APPLICATION_JSON).principal(principal))
        .andExpect(status().isBadRequest());

    mvc.perform(get(
        "http://localhost:8888/api/sensors/{projectId}/{deviceType}/{crackdisplacementDeviceId}",
        projectId, deviceType, crackdisplacementDeviceId).param("size", size)
            .param("startDate", "2019-07-01T21:02:00").param("endDate", "2019-07-01T21:00:00")
            .contextPath("/api").accept(MediaType.APPLICATION_JSON).principal(principal))
        .andExpect(status().isBadRequest());

    verifyNoMoreInteractions(iotSensorDataService);
  }

  @Test
  @WithMockUser
  public void getDeviceConfiguration_Returns_BadRequest_WrongSize() throws Exception {
    String size = "size";

    mvc.perform(get(
        "http://localhost:8888/api/sensors/{projectId}/{deviceType}/{crackdisplacementDeviceId}",
        projectId, deviceType, crackdisplacementDeviceId).param("size", size)
            .param("startDate", "2019-07-01T21:00:00").param("endDate", "2019-07-01T21:02:00")
            .contextPath("/api").accept(MediaType.APPLICATION_JSON).principal(principal))
        .andExpect(status().isBadRequest());

    verifyNoMoreInteractions(iotSensorDataService);
  }

  @Test
  @WithMockUser
  public void putDeviceConfiguration_Returns_ResultSet() throws Exception {
    DeviceDataDto deviceData = new DeviceDataDto();
    deviceData.setSensorType(sensorType);
    String json = new Gson().toJson(deviceData);
    when(iotSensorDataService.putDeviceConfiguration(deviceType, crackdisplacementDeviceId, json))
        .thenReturn(deviceData);

    mvc.perform(put(
        "http://localhost:8888/api/sensors/{projectId}/{deviceType}/{crackdisplacementDeviceId}",
        projectId, deviceType, crackdisplacementDeviceId).contentType(MediaType.APPLICATION_JSON)
            .content(json).contextPath("/api").accept(MediaType.APPLICATION_JSON)
            .principal(principal))
        .andExpect(status().isOk()).andExpect(jsonPath("$").isNotEmpty())
        .andExpect(jsonPath("$.sensorType").isNotEmpty());

    verify(iotSensorDataService, times(1)).putDeviceConfiguration(deviceType,
        crackdisplacementDeviceId, json);
    verifyNoMoreInteractions(iotSensorDataService);
  }

  @Test
  @WithMockUser
  public void putDeviceConfiguration_Returns_BadRequest() throws Exception {
    DeviceDataDto deviceData = new DeviceDataDto();
    deviceData.setSensorType(sensorType);
    String json = new Gson().toJson(deviceData);
    when(iotSensorDataService.putDeviceConfiguration(deviceType, crackdisplacementDeviceId, json))
        .thenReturn(null);

    mvc.perform(put(
        "http://localhost:8888/api/sensors/{projectId}/{deviceType}/{crackdisplacementDeviceId}",
        projectId, deviceType, crackdisplacementDeviceId).contentType(MediaType.APPLICATION_JSON)
            .content(json).contextPath("/api").accept(MediaType.APPLICATION_JSON)
            .principal(principal))
        .andExpect(status().isBadRequest());

    verify(iotSensorDataService, times(1)).putDeviceConfiguration(deviceType,
        crackdisplacementDeviceId, json);
    verifyNoMoreInteractions(iotSensorDataService);
  }

  @Test
  @WithMockUser
  public void getGraphForDevice_Returns_ResultSet() throws Exception {
    String size = "maxview";
    DeviceDataGraphDto graphData = mockGraphData();
    LocalDateTime startDate = LocalDateTime.of(2019, 7, 1, 21, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2019, 7, 1, 21, 02, 00);

    when(iotSensorDataService.getDeviceDataForGraph(deviceType, crackdisplacementDeviceId,
        startDate, endDate, size)).thenReturn(graphData);

    mvc.perform(get(
        "http://localhost:8888/api/sensors/{projectId}/{deviceType}/{crackdisplacementDeviceId}/graph",
        projectId, deviceType, crackdisplacementDeviceId).param("startDate", "2019-07-01T21:00:00")
            .param("endDate", "2019-07-01T21:02:00").param("size", size).contextPath("/api")
            .accept(MediaType.APPLICATION_JSON).principal(principal))
        .andExpect(status().isOk());

    verify(iotSensorDataService, times(1)).getDeviceDataForGraph(deviceType,
        crackdisplacementDeviceId, startDate, endDate, size);
    verifyNoMoreInteractions(iotSensorDataService);
  }

  @Test
  @WithMockUser
  public void getGraphForDevice_Returns_DeviceNotFound() throws Exception {
    String size = "maxview";
    LocalDateTime startDate = LocalDateTime.of(2019, 7, 1, 21, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2019, 7, 1, 21, 02, 00);

    when(iotSensorDataService.getDeviceDataForGraph(deviceType, crackdisplacementDeviceId,
        startDate, endDate, null)).thenReturn(null);

    mvc.perform(get(
        "http://localhost:8888/api/sensors/{projectId}/{deviceType}/{crackdisplacementDeviceId}/graph",
        projectId, deviceType, crackdisplacementDeviceId).param("startDate", "2019-07-01T21:00:00")
            .param("endDate", "2019-07-01T21:02:00").param("size", size).contextPath("/api")
            .accept(MediaType.APPLICATION_JSON).principal(principal))
        .andExpect(status().isNotFound());

    verify(iotSensorDataService, times(1)).getDeviceDataForGraph(deviceType,
        crackdisplacementDeviceId, startDate, endDate, size);
    verifyNoMoreInteractions(iotSensorDataService);
  }

  @Test
  @WithMockUser
  public void getGraphForDevice_Returns_BadRequest() throws Exception {
    String size = "maxview";
    LocalDateTime startDate = LocalDateTime.of(2019, 7, 1, 21, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2019, 7, 1, 21, 02, 00);

    when(iotSensorDataService.getDeviceDataForGraph(deviceType, crackdisplacementDeviceId,
        startDate, endDate, null)).thenReturn(null);

    mvc.perform(get(
        "http://localhost:8888/api/sensors/{projectId}/{deviceType}/{crackdisplacementDeviceId}/graph",
        projectId, deviceType, crackdisplacementDeviceId).param("startDate", "2019-07-01T21:00:00")
            .param("size", size).contextPath("/api").accept(MediaType.APPLICATION_JSON)
            .principal(principal))
        .andExpect(status().isBadRequest());

    mvc.perform(get(
        "http://localhost:8888/api/sensors/{projectId}/{deviceType}/{crackdisplacementDeviceId}/graph",
        projectId, deviceType, crackdisplacementDeviceId).param("endDate", "2019-07-01T21:02:00")
            .contextPath("/api").accept(MediaType.APPLICATION_JSON).principal(principal))
        .andExpect(status().isBadRequest());

    mvc.perform(get(
        "http://localhost:8888/api/sensors/{projectId}/{deviceType}/{crackdisplacementDeviceId}/graph",
        projectId, deviceType, crackdisplacementDeviceId).param("endDate", "2019-07-01T21:00:00")
            .param("startDate", "2019-07-01T21:02:00").contextPath("/api")
            .accept(MediaType.APPLICATION_JSON).principal(principal))
        .andExpect(status().isBadRequest());

    verifyNoMoreInteractions(iotSensorDataService);
  }

  @Test
  @WithMockUser
  public void getGraphForDevice_Returns_BadRequest_WrongSize() throws Exception {
    String size = "maview";
    LocalDateTime startDate = LocalDateTime.of(2019, 7, 1, 21, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2019, 7, 1, 21, 02, 00);

    when(iotSensorDataService.getDeviceDataForGraph(deviceType, crackdisplacementDeviceId,
        startDate, endDate, null)).thenReturn(null);

    mvc.perform(get(
        "http://localhost:8888/api/sensors/{projectId}/{deviceType}/{crackdisplacementDeviceId}/graph",
        projectId, deviceType, crackdisplacementDeviceId).param("startDate", "2019-07-01T21:00:00")
            .param("endDate", "2019-07-01T21:02:00").param("size", size).contextPath("/api")
            .accept(MediaType.APPLICATION_JSON).principal(principal))
        .andExpect(status().isBadRequest());

    verifyNoMoreInteractions(iotSensorDataService);
  }
  
  @Test
  @WithMockUser
  public void getHighAndLowValuesForSensor_Returns_ResultSet() throws Exception {
    CrackSensorDto highLowCrackSensor = new CrackSensorDto();
    HighLowValuesDto highLowValues = mockHighLowValuesForSensor();
    highLowCrackSensor.setHighLowValues_crack(highLowValues);

    when(iotSensorDataService.getHighLowValuesCrackdisplacement(Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.matches(crackdisplacementDeviceId)))
            .thenReturn(highLowCrackSensor);

    mvc.perform(get(
        "http://localhost:8888/api/sensors/1/Crk/{crackdisplacementDeviceId}/minmax",
        crackdisplacementDeviceId).contextPath("/api").accept(MediaType.APPLICATION_JSON)
            .principal(principal))
        .andExpect(status().isOk()).andExpect(jsonPath("$").isNotEmpty());

    verify(iotSensorDataService, times(1)).getHighLowValuesCrackdisplacement(
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class),
        Mockito.matches(crackdisplacementDeviceId));
    verifyNoMoreInteractions(iotSensorDataService);
  }

  @Test
  @WithMockUser
  public void getHighAndLowValuesForSensor_Returns_EmptyResultSet() throws Exception {

    when(iotSensorDataService.getHighLowValuesCrackdisplacement(Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.matches(crackdisplacementDeviceId)))
            .thenReturn(null);

    mvc.perform(get("http://localhost:8888/api/sensors/1/Crk/{deviceId}/minmax",
        crackdisplacementDeviceId).contextPath("/api").accept(MediaType.APPLICATION_JSON)
            .principal(principal))
        .andExpect(status().isNotFound());

    verify(iotSensorDataService, times(1)).getHighLowValuesCrackdisplacement(
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class),
        Mockito.matches(crackdisplacementDeviceId));
    verifyNoMoreInteractions(iotSensorDataService);
  }

  @Test
  @WithMockUser
  public void getHighLowValuesForAccelerometer_Returns_ResultSet() throws Exception {
    AccelerometerDto highLowAcc = new AccelerometerDto();
    HighLowValuesDto highLowValues = mockHighLowValuesForSensor();
    highLowAcc.setHighLowValues_x(highLowValues);

    when(iotSensorDataService.getHighLowValuesAccelerometer(Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.matches(accelerometerDeviceId)))
            .thenReturn(highLowAcc);

    mvc.perform(
        get("http://localhost:8888/api/sensors/1/accelerometer/{accelerometerDeviceId}/minmax",
            accelerometerDeviceId).contextPath("/api").accept(MediaType.APPLICATION_JSON)
                .principal(principal))
        .andExpect(status().isOk());

    verify(iotSensorDataService, times(1)).getHighLowValuesAccelerometer(
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class),
        Mockito.matches(accelerometerDeviceId));
    verifyNoMoreInteractions(iotSensorDataService);
  }

  @Test
  @WithMockUser
  public void getHighLowValuesForAccelerometer_Returns_EmptyResultSet() throws Exception {

    when(iotSensorDataService.getHighLowValuesAccelerometer(Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.matches(accelerometerDeviceId))).thenReturn(null);

    mvc.perform(
        get("http://localhost:8888/api/sensors/1/accelerometer/{accelerometerDeviceId}/minmax",
            accelerometerDeviceId).contextPath("/api").accept(MediaType.APPLICATION_JSON)
                .principal(principal))
        .andExpect(status().isNotFound());

    verify(iotSensorDataService, times(1)).getHighLowValuesAccelerometer(
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class),
        Mockito.matches(accelerometerDeviceId));
    verifyNoMoreInteractions(iotSensorDataService);

  }

  private DeviceDataGraphDto mockGraphData() {
    DeviceDataGraphDto graphData = new DeviceDataGraphDto();
    List<SensorDataDto> sensorDataList = new ArrayList<>();
    SensorDataDto sensorData = new SensorDataDto();
    sensorData.setTitle("crack");
    sensorData.setUnitOfMeasurement("mm");
    List<DataDto> dataList = new ArrayList<>();
    DataDto measurementData = new DataDto();
    measurementData.setValue(0.123f);
    measurementData.setTimestamp(LocalDateTime.now().toString());
    dataList.add(measurementData);
    sensorData.setData(dataList);
    graphData.setDeviceId("Crk01");
    graphData.setDeviceType("Crk");
    graphData.setSensorData(sensorDataList);

    return graphData;
  }
  
  private HighLowValuesDto mockHighLowValuesForSensor() {
    HighLowValuesDto highLowValues = new HighLowValuesDto();
    highLowValues.setLowValue(-0.0042045f);
    highLowValues.setLowTimestamp(LocalDateTime.of(2019, 03, 11, 11, 21, 10).toString());
    highLowValues.setHighValue(0.0051604f);
    highLowValues.setHighTimestamp(LocalDateTime.of(2019, 03, 11, 11, 20, 35).toString());

    return highLowValues;
  }

}
