package com.opera.iotapisensors.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
      .cors().and()
      .csrf()
        .disable()
      .authorizeRequests()
        .antMatchers(
          "/actuator/**",
          "/swagger-ui.html", "/webjars/springfox-swagger-ui/**", "/swagger-resources/**", "/v2/api-docs"
          )
        .permitAll()
        .and()
      .authorizeRequests()
        .anyRequest()
        .authenticated()
        .and()
      .oauth2ResourceServer()
        .jwt();
    // @formatter:on
  }
}
