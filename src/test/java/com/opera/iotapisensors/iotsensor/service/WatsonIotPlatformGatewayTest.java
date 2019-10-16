package com.opera.iotapisensors.iotsensor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import com.opera.iotapisensors.config.RestTemplateConfig;
import com.opera.iotapisensors.iotsensor.enums.AlertLevel;
import com.opera.iotapisensors.iotsensor.enums.DeviceType;

@RunWith(SpringRunner.class)
@RestClientTest
@ContextConfiguration(classes = {RestTemplateConfig.class, WatsonIotPlatformGateway.class})
@TestPropertySource(properties = {
    "rtmt.watsoniot.server=localhost/alertLevel/",
    "rtmt.watsoniot.clientid=testClientId",
    "rtmt.watsoniot.alertLogicalInterface=alertLogicalInterface"
    })
public class WatsonIotPlatformGatewayTest {
  @Autowired
  private WatsonIotPlatformGateway underTest;
  
  @Autowired
  private MockRestServiceServer server;
  
  @Test
  public void testGetAlertLevel() throws Exception {
    // Given:
    AlertLevel expectedResult = AlertLevel.CRITICAL;
    DeviceType expectedDeviceType = DeviceType.INCLINOMETER;
    String expectedDeviceId = "did";
    String expectedAlertLogicalInterface = "alertLogicalInterface";
    String expectedUrl = String.format("https://testClientId.localhost/alertLevel/device/types/%s/devices/%s/state/%s", expectedDeviceType, expectedDeviceId, expectedAlertLogicalInterface);
    String alertLevelResponse = "{\r\n" + 
        "  \"timestamp\": \"2019-09-02T08:23:03Z\",\r\n" + 
        "  \"updated\": \"2019-09-02T08:23:03Z\",\r\n" + 
        "  \"state\": {\r\n" + 
        "    \"alertLevel\": 1,\r\n" + 
        "    \"magnitude\": 22.48792,\r\n" + 
        "    \"isWarning\": false,\r\n" + 
        "    \"isNormal\": false,\r\n" + 
        "    \"isCritical\": true,\r\n" + 
        "    \"blockDb2\": [\r\n" + 
        "      1,\r\n" + 
        "      2\r\n" + 
        "    ],\r\n" + 
        "    \"measuredAt\": \"2019-09-02T10:27:04.736+02:00\"\r\n" + 
        "  }\r\n" + 
        "}";
    server.expect(requestTo(expectedUrl)).andRespond(withSuccess(alertLevelResponse, MediaType.APPLICATION_JSON));

    // When:
    AlertLevel actualResult = underTest.getAlertLevel(expectedDeviceType, expectedDeviceId);

    // Then:
    assertThat(actualResult).isEqualTo(expectedResult);
  }

}
 