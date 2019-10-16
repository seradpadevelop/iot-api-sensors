package com.opera.iotapisensors.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


/**
 * This class disables cors restrictions, allowing front end request from any origin.
 * 
 * The class relies on an existing WebSecurityConfigurerAdapter which configures a HttpSecurity object
 * and enables cors support like this:
 * 
 * <pre>
 * <code>@Override</code>
 * protected void configure(final HttpSecurity http) throws Exception {
 *   http.cors();
 * }
 * </pre>
 * 
 * @author Aaron Tavio
 */
@Configuration
@Profile({"local-dev", "ibmcloud-dev"})
public class CorsAllowAllOriginsDevConfig {
  @Bean
  @Primary
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfig = new CorsConfiguration().applyPermitDefaultValues();
    corsConfig.setAllowedMethods(this.getAllMethods());
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);
    return source;
  }
  
  private List<String> getAllMethods() {
    return Arrays.asList(HttpMethod.values()).stream().map(HttpMethod::name).collect(Collectors.toList());
  }
}
