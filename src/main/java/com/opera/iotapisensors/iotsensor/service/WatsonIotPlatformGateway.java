package com.opera.iotapisensors.iotsensor.service;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opera.iotapisensors.iotsensor.enums.AlertLevel;
import com.opera.iotapisensors.iotsensor.enums.DeviceType;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class WatsonIotPlatformGateway {
  @Value("${rtmt.watsoniot.clientid}")
  private String clientId;
  @Value("${rtmt.watsoniot.server}")
  private String server;
  @Value("${rtmt.watsoniot.apikey}")
  private String apikey;
  @Value("${rtmt.watsoniot.token}")
  private String token;
  @Value("${rtmt.watsoniot.alertLogicalInterface:AlertLevelInterface}")
  private String alertLogicalInterface;
  
  @Autowired
  private RestTemplate restTemplate;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  
  
  public URI getUri(String path, Map<String,String> urlParams) {
    UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(String.format("https://%s.%s", clientId, server)).path(path);
    return urlBuilder.buildAndExpand(urlParams).toUri();
  }
  
  private String getAuthenticationToken() {
    String auth = apikey + ":" + token;
    byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
    return "Basic " + new String(encodedAuth);
  }
  
  private HttpHeaders getDefaultHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", getAuthenticationToken());
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }
  
  public @Nullable AlertLevel getAlertLevel(DeviceType deviceType, String deviceId) {
    final String alertLevelServiceUri = "device/types/{deviceType}/devices/{deviceId}/state/{logicalInterface}";
    AlertLevel alertLevel = null;
    
    Map<String, String> urlParams = new HashMap<String, String>();
    urlParams.put("deviceType", deviceType.getLabel());
    urlParams.put("deviceId", deviceId);
    urlParams.put("logicalInterface", alertLogicalInterface);
    
    // @formatter:off
    ResponseEntity<String> response = restTemplate.exchange(
        getUri(alertLevelServiceUri, urlParams),
        HttpMethod.GET,
        new HttpEntity<>(getDefaultHeaders()),
        String.class);
    // @formatter:on
    
    try {
      if (response.getStatusCode().isError()) {
        throw new ResponseStatusException(response.getStatusCode(), response.getBody());
      }
    
      JsonNode jsonNode = objectMapper.readTree(response.getBody());
      alertLevel = deserializeAlertLevel(jsonNode.get("state"));
    } catch (Exception e) {
      log.error(String.format("Error while obtaining alert level for deviceType: %s, deviceId: %s", deviceType, deviceId), e);
    }
    
    return alertLevel;
  }

  private @Nullable AlertLevel deserializeAlertLevel(JsonNode state) {
    if(state.get("isWarning") == null || state.get("isCritical") == null || state.get("isNormal") == null){
      return null;
    }
    if (state.get("isWarning").asBoolean()) {
      return AlertLevel.WARNING;
    } else if (state.get("isCritical").asBoolean()) {
      return AlertLevel.CRITICAL;
    } else {
      return AlertLevel.NORMAL;
    }
  }

}
