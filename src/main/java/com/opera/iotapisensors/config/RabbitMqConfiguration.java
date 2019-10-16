package com.opera.iotapisensors.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.SimplePropertyValueConnectionNameStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.opera.iotapisensors.events.DomainEventPublisher;

@Configuration
public class RabbitMqConfiguration {

  @Value("${spring.rabbitmq.exchange}")
  private String exchangeName;

  @Value("${HOSTNAME:iot-api-sensors}")
  private String hostname;

  @Bean
  public TopicExchange exchange() {
    return new TopicExchange(exchangeName);
  }

  @Bean
  public DomainEventPublisher sender() {
    return new DomainEventPublisher();
  }

  @Bean
  public ConnectionNameStrategy connectionNameStrategy() {
    return new SimplePropertyValueConnectionNameStrategy(hostname);
  }
}
