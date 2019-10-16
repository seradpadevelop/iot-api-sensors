package com.opera.iotapisensors.iotsensor.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opera.iotapisensors.iotsensor.dto.DeviceDataDto;
import com.opera.iotapisensors.iotsensor.dto.DeviceDataMapper;
import com.opera.iotapisensors.iotsensor.enums.DeviceType;
import junit.framework.TestCase;

@RunWith(SpringRunner.class)
public class DeviceManagementServiceTest extends TestCase {

  @InjectMocks
  DeviceManagementService deviceManagementService;

  @Mock
  Logger logger = LoggerFactory.getLogger(DeviceManagementService.class);

  @Mock
  DeviceDataMapper mapper;

  @Mock
  RestTemplate restTemplate;

  DeviceType deviceType = DeviceType.CRACK;
  String deviceId = "Crk01";

  @Test
  public void getDeviceConfiguration_Returns_ResultSet() throws Exception {
    String deviceDataString = "{\"deviceId\":\"" + deviceId + "\"}";
    ResponseEntity<String> response = new ResponseEntity<>(deviceDataString, HttpStatus.OK);
    DeviceDataDto deviceDataDto = new DeviceDataDto();
    deviceDataDto.setDeviceId(deviceId);

    when(restTemplate.exchange(Mockito.anyString(), eq(HttpMethod.GET),
        Mockito.any(HttpEntity.class), eq(String.class))).thenReturn(response);

    when(mapper.toDto(deviceDataString)).thenReturn(deviceDataDto);

    DeviceDataDto result = deviceManagementService.getDeviceConfiguration(deviceType, deviceId);

    Assert.assertNotNull(result);
    Assert.assertEquals(deviceId, result.getDeviceId());

    Mockito.verify(restTemplate, times(1)).exchange(Mockito.anyString(), eq(HttpMethod.GET),
        Mockito.any(HttpEntity.class), eq(String.class));
    Mockito.verify(mapper, times(1)).toDto(deviceDataString);
    Mockito.verify(logger, times(1)).debug(Mockito.anyString());
    Mockito.verifyNoMoreInteractions(restTemplate, mapper, logger);
  }

  @Test
  public void getDeviceConfiguration_Returns_ExchangeError() throws Exception {
    doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND)).when(restTemplate).exchange(
        Mockito.anyString(), eq(HttpMethod.GET), Mockito.any(HttpEntity.class), eq(String.class));

    DeviceDataDto result = deviceManagementService.getDeviceConfiguration(deviceType, deviceId);

    Assert.assertNull(result);

    Mockito.verify(restTemplate, times(1)).exchange(Mockito.anyString(), eq(HttpMethod.GET),
        Mockito.any(HttpEntity.class), eq(String.class));
    Mockito.verify(logger, times(1)).error(Mockito.anyString());
    Mockito.verifyNoMoreInteractions(restTemplate, mapper, logger);
  }

  @Test
  public void getDeviceConfiguration_Returns_MappingError() throws Exception {
    String deviceDataString = "{\"deviceId\":\"" + deviceId + "\"}";
    ResponseEntity<String> response = new ResponseEntity<>(deviceDataString, HttpStatus.OK);
    DeviceDataDto deviceDataDto = new DeviceDataDto();
    deviceDataDto.setDeviceId(deviceId);

    when(restTemplate.exchange(Mockito.anyString(), eq(HttpMethod.GET),
        Mockito.any(HttpEntity.class), eq(String.class))).thenReturn(response);

    doThrow(new Exception("pebcak")).when(mapper).toDto(deviceDataString);

    DeviceDataDto result = deviceManagementService.getDeviceConfiguration(deviceType, deviceId);

    Assert.assertNull(result);

    Mockito.verify(restTemplate, times(1)).exchange(Mockito.anyString(), eq(HttpMethod.GET),
        Mockito.any(HttpEntity.class), eq(String.class));
    Mockito.verify(mapper, times(1)).toDto(deviceDataString);
    Mockito.verify(logger, times(1)).debug(Mockito.anyString());
    Mockito.verify(logger, times(1)).error(Mockito.anyString());
    Mockito.verifyNoMoreInteractions(restTemplate, mapper, logger);
  }

  @Test
  public void putDeviceConfiguration_Returns_ResultSet() throws Exception {
    String deviceDataString = "{\"deviceId\":\"" + deviceId + "\"}";
    JsonObject deviceDataJson = new JsonParser().parse(deviceDataString).getAsJsonObject();
    ResponseEntity<String> response = new ResponseEntity<>(deviceDataString, HttpStatus.OK);
    DeviceDataDto deviceDataDto = new DeviceDataDto();
    deviceDataDto.setDeviceId(deviceId);

    when(restTemplate.exchange(Mockito.anyString(), eq(HttpMethod.GET),
        Mockito.any(HttpEntity.class), eq(String.class))).thenReturn(response);

    when(restTemplate.exchange(Mockito.anyString(), eq(HttpMethod.PUT),
        Mockito.any(HttpEntity.class), eq(String.class))).thenReturn(response);

    when(mapper.updateDeviceData(deviceDataString, deviceDataJson)).thenReturn(deviceDataString);

    when(mapper.toDto(deviceDataString)).thenReturn(deviceDataDto);

    DeviceDataDto result =
        deviceManagementService.putDeviceConfiguration(deviceType, deviceId, deviceDataJson);

    Assert.assertNotNull(result);
    Assert.assertEquals(deviceId, result.getDeviceId());

    Mockito.verify(restTemplate, times(1)).exchange(Mockito.anyString(), eq(HttpMethod.GET),
        Mockito.any(HttpEntity.class), eq(String.class));
    Mockito.verify(restTemplate, times(1)).exchange(Mockito.anyString(), eq(HttpMethod.PUT),
        Mockito.any(HttpEntity.class), eq(String.class));
    Mockito.verify(mapper, times(1)).updateDeviceData(deviceDataString, deviceDataJson);
    Mockito.verify(mapper, times(1)).toDto(deviceDataString);
    Mockito.verify(logger, times(1)).debug(Mockito.anyString());
    Mockito.verifyNoMoreInteractions(restTemplate, mapper, logger);
  }

  @Test
  public void putDeviceConfiguration_Returns_GetExchangeError() throws Exception {
    String deviceDataString = "{\"deviceId\":\"" + deviceId + "\"}";
    JsonObject deviceDataJson = new JsonParser().parse(deviceDataString).getAsJsonObject();

    doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND)).when(restTemplate).exchange(
        Mockito.anyString(), eq(HttpMethod.GET), Mockito.any(HttpEntity.class), eq(String.class));

    DeviceDataDto result =
        deviceManagementService.putDeviceConfiguration(deviceType, deviceId, deviceDataJson);

    Assert.assertNull(result);

    Mockito.verify(restTemplate, times(1)).exchange(Mockito.anyString(), eq(HttpMethod.GET),
        Mockito.any(HttpEntity.class), eq(String.class));
    Mockito.verify(logger, times(1)).error(Mockito.anyString());
    Mockito.verifyNoMoreInteractions(restTemplate, mapper, logger);
  }

  @Test
  public void putDeviceConfiguration_Returns_PutExchangeErrort() throws Exception {
    String deviceDataString = "{\"deviceId\":\"" + deviceId + "\"}";
    JsonObject deviceDataJson = new JsonParser().parse(deviceDataString).getAsJsonObject();
    ResponseEntity<String> response = new ResponseEntity<>(deviceDataString, HttpStatus.OK);
    DeviceDataDto deviceDataDto = new DeviceDataDto();
    deviceDataDto.setDeviceId(deviceId);

    when(restTemplate.exchange(Mockito.anyString(), eq(HttpMethod.GET),
        Mockito.any(HttpEntity.class), eq(String.class))).thenReturn(response);

    doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND)).when(restTemplate).exchange(
        Mockito.anyString(), eq(HttpMethod.PUT), Mockito.any(HttpEntity.class), eq(String.class));

    when(mapper.updateDeviceData(deviceDataString, deviceDataJson)).thenReturn(deviceDataString);

    DeviceDataDto result =
        deviceManagementService.putDeviceConfiguration(deviceType, deviceId, deviceDataJson);

    Assert.assertNull(result);

    Mockito.verify(restTemplate, times(1)).exchange(Mockito.anyString(), eq(HttpMethod.GET),
        Mockito.any(HttpEntity.class), eq(String.class));
    Mockito.verify(restTemplate, times(1)).exchange(Mockito.anyString(), eq(HttpMethod.PUT),
        Mockito.any(HttpEntity.class), eq(String.class));
    Mockito.verify(mapper, times(1)).updateDeviceData(deviceDataString, deviceDataJson);
    Mockito.verify(logger, times(1)).error(Mockito.anyString());
    Mockito.verifyNoMoreInteractions(restTemplate, mapper, logger);
  }


  @Test
  public void putDeviceConfiguration_Returns_MappingError1() throws Exception {
    String deviceDataString = "{\"deviceId\":\"" + deviceId + "\"}";
    JsonObject deviceDataJson = new JsonParser().parse(deviceDataString).getAsJsonObject();
    ResponseEntity<String> response = new ResponseEntity<>(deviceDataString, HttpStatus.OK);
    DeviceDataDto deviceDataDto = new DeviceDataDto();
    deviceDataDto.setDeviceId(deviceId);

    when(restTemplate.exchange(Mockito.anyString(), eq(HttpMethod.GET),
        Mockito.any(HttpEntity.class), eq(String.class))).thenReturn(response);

    doThrow(new Exception("pebcak")).when(mapper).updateDeviceData(deviceDataString, deviceDataJson);

    DeviceDataDto result =
        deviceManagementService.putDeviceConfiguration(deviceType, deviceId, deviceDataJson);

    Assert.assertNull(result);

    Mockito.verify(restTemplate, times(1)).exchange(Mockito.anyString(), eq(HttpMethod.GET),
        Mockito.any(HttpEntity.class), eq(String.class));
    Mockito.verify(mapper, times(1)).updateDeviceData(deviceDataString, deviceDataJson);
    Mockito.verify(logger, times(1)).error(Mockito.anyString());
    Mockito.verifyNoMoreInteractions(restTemplate, mapper, logger);
  }

  @Test
  public void putDeviceConfiguration_Returns_MappingError2() throws Exception {
    String deviceDataString = "{\"deviceId\":\"" + deviceId + "\"}";
    JsonObject deviceDataJson = new JsonParser().parse(deviceDataString).getAsJsonObject();
    ResponseEntity<String> response = new ResponseEntity<>(deviceDataString, HttpStatus.OK);
    DeviceDataDto deviceDataDto = new DeviceDataDto();
    deviceDataDto.setDeviceId(deviceId);

    when(restTemplate.exchange(Mockito.anyString(), eq(HttpMethod.GET),
        Mockito.any(HttpEntity.class), eq(String.class))).thenReturn(response);

    when(restTemplate.exchange(Mockito.anyString(), eq(HttpMethod.PUT),
        Mockito.any(HttpEntity.class), eq(String.class))).thenReturn(response);

    when(mapper.updateDeviceData(deviceDataString, deviceDataJson)).thenReturn(deviceDataString);

    doThrow(new Exception("pebcak")).when(mapper).toDto(deviceDataString);

    DeviceDataDto result =
        deviceManagementService.putDeviceConfiguration(deviceType, deviceId, deviceDataJson);

    Assert.assertNull(result);

    Mockito.verify(restTemplate, times(1)).exchange(Mockito.anyString(), eq(HttpMethod.GET),
        Mockito.any(HttpEntity.class), eq(String.class));
    Mockito.verify(restTemplate, times(1)).exchange(Mockito.anyString(), eq(HttpMethod.PUT),
        Mockito.any(HttpEntity.class), eq(String.class));
    Mockito.verify(mapper, times(1)).updateDeviceData(deviceDataString, deviceDataJson);
    Mockito.verify(mapper, times(1)).toDto(deviceDataString);
    Mockito.verify(logger, times(1)).debug(Mockito.anyString());
    Mockito.verify(logger, times(1)).error(Mockito.anyString());
    Mockito.verifyNoMoreInteractions(restTemplate, mapper, logger);
  }
}
