package com.opera.iotapisensors.iotsensor.service;

import java.text.MessageFormat;
import java.util.Collections;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.google.gson.JsonObject;
import com.opera.iotapisensors.iotsensor.dto.DeviceDataDto;
import com.opera.iotapisensors.iotsensor.dto.DeviceDataMapper;
import com.opera.iotapisensors.iotsensor.enums.DeviceType;

@Component
@Service
public class DeviceManagementService {

  @Autowired
  DeviceDataMapper mapper;

  @Autowired
  private RestTemplate restTemplate;

  private Logger logger = LoggerFactory.getLogger(DeviceManagementService.class);

  @Value("${rtmt.watsoniot.server}")
  String server;

  @Value("${rtmt.watsoniot.apikey}")
  String apikey;

  @Value("${rtmt.watsoniot.token}")
  String token;

  @Value("${rtmt.watsoniot.clientid}")
  String clientid;

  /**
   * Get the device configuration from the Watson IoT platform.
   * 
   * @param deviceType type of the device
   * @param deviceId unique identification string of the device
   * @return the object that is stored in the platform
   */
  public DeviceDataDto getDeviceConfiguration(DeviceType deviceType, String deviceId) {
    String targetUrl = createTargetUrl(deviceType, deviceId);
    HttpHeaders headers = createAuthorizationHeader();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<String> response = null;

    try {
      response = restTemplate.exchange(targetUrl, HttpMethod.GET, entity, String.class);
    } catch (HttpClientErrorException e) {
      logger.error(MessageFormat.format(
          "Error {0} while getting device data for the deviceType = {1} and deviceId = {2}",
          e.getStatusCode(), deviceType, deviceId));
      return null;
    }

    logger.debug("Watson IoT platform response: " + response.toString());

    // map platform response to our device data model
    DeviceDataDto result = new DeviceDataDto();
    try {
      result = mapper.toDto(response.getBody());
    } catch (Exception e) {
      logger.error("Error in DeviceDataMapper: " + e.toString());
      result = null;
    }
    return result;
  }

  
  /**
   * Update the device configuration in the Watson IoT platform.
   * 
   * @param deviceType type of the device
   * @param deviceId unique identification string of the device
   * @param deviceData the changed device configuration
   * @return the object that has been stored in the platform
   */
  public DeviceDataDto putDeviceConfiguration(DeviceType deviceType, String deviceId,
      JsonObject deviceData) {

    String targetUrl = createTargetUrl(deviceType, deviceId);
    HttpHeaders headers = createAuthorizationHeader();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);

    // get the old object state from the Watson IoT platform
    HttpEntity<String> getEntity = new HttpEntity<>(headers);
    ResponseEntity<String> getResponse = null;

    try {
      getResponse = restTemplate.exchange(targetUrl, HttpMethod.GET, getEntity, String.class);
    } catch (HttpClientErrorException e) {
      logger.error(MessageFormat.format(
          "Error {0} while getting device data for the deviceType = {1} and deviceId = {2}",
          e.getStatusCode(), deviceType, deviceId));
      return null;
    }

    // apply the changes to old object state
    String requestString = new String();
    try {
      requestString = mapper.updateDeviceData(getResponse.getBody(), deviceData);
    } catch (Exception e) {
      logger.error("Error in DeviceDataMapper: " + e.getMessage());
      return null;
    }

    // put the new object state to the Watson IoT platform
    HttpEntity<String> putEntity = new HttpEntity<>(requestString, headers);
    ResponseEntity<String> putResponse = null;

    try {
      putResponse = restTemplate.exchange(targetUrl, HttpMethod.PUT, putEntity, String.class);
    } catch (HttpClientErrorException e) {
      logger.error(MessageFormat.format(
          "Error {0} while putting device data for the deviceType = {1} and deviceId = {2}, payload is: {3}",
          e.getStatusCode(), deviceType, deviceId, requestString));
      return null;
    }

    logger.debug("Watson IoT platform response: " + putResponse.toString());

    // map platform response to our device data model
    DeviceDataDto result = new DeviceDataDto();
    try {
      result = mapper.toDto(putResponse.getBody());
    } catch (Exception e) {
      logger.error("Error in DeviceDataMapper: " + e.getMessage());
      return null;
    }
    return result;
  }

  protected HttpHeaders createAuthorizationHeader() {
    return new HttpHeaders() {
      private static final long serialVersionUID = -642654526949759951L;
      {
        String auth = apikey + ":" + token;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        set("Authorization", authHeader);
      }
    };
  }

  private String createTargetUrl(DeviceType deviceType, String deviceId) {
    return "https://" + clientid + "." + server + "device/types/" + deviceType.getLabel() + "/devices/"
        + deviceId;
  }
}
