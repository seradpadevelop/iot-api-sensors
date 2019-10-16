package com.opera.iotapisensors.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingFilterConfiguration {

  @Bean
  @ConditionalOnProperty(prefix = "logging.level", name = "org.springframework.web.filter.CommonsRequestLoggingFilter", havingValue = "DEBUG", matchIfMissing = false)
  public CommonsRequestLoggingFilter requestLoggingFilter() {
    CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();

    loggingFilter.setIncludeClientInfo(true);
    loggingFilter.setIncludeQueryString(true);
    loggingFilter.setIncludePayload(true);
    loggingFilter.setIncludeHeaders(true);

    return loggingFilter;
  }


}
