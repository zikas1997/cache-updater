package ru.zikas1997.cacheupdater.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.zikas1997.cacheupdater.bpp.CacheBeanPostProcessor;
import ru.zikas1997.cacheupdater.service.MQSender;
import ru.zikas1997.cacheupdater.service.impl.RabbitMQSenderImpl;

@Configuration
public class ConfigurationApp2 {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Bean
    public MQSender rabbitMQSenderImpl() {
        return new RabbitMQSenderImpl(amqpTemplate);
    }

    @Bean
    public BeanPostProcessor cacheBeanPostProcessor() {
        return new CacheBeanPostProcessor(rabbitMQSenderImpl());
    }

}
