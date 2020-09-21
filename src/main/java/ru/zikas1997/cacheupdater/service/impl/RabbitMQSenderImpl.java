package ru.zikas1997.cacheupdater.service.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.amqp.core.AmqpTemplate;
import ru.zikas1997.cacheupdater.model.MqMessage;
import ru.zikas1997.cacheupdater.service.MQSender;

@Data
@AllArgsConstructor
public class RabbitMQSenderImpl implements MQSender {

    private final AmqpTemplate  jmsTemplate;

    @Override
    public void send(MqMessage mqMessage) {
        jmsTemplate.convertAndSend("exchange-cache","",mqMessage);
    }

}

