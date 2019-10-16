package com.opera.iotapisensors.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import com.opera.iotapisensors.config.RabbitMqConfiguration;

@ComponentScan(basePackageClasses = {RabbitMqConfiguration.class})
@Component
public class DomainEventPublisher {

  @Value("${spring.rabbitmq.exchange}")
  public String exchangeName;
  @Value("${spring.rabbitmq.routingKey}")
  public String routingKey;

  @Autowired
  private RabbitTemplate template;

  @Autowired
  private TopicExchange exchange;

  private Logger logger = LoggerFactory.getLogger(DomainEventPublisher.class);

  public void publishRabbitmqEvent(String message) throws Exception {
    template.setExchange(exchangeName);
    template.convertAndSend(exchange.getName(), routingKey, message);

    logger.debug(" [x] Sent '" + routingKey + "':'" + message + "'");
  }
}
