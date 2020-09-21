package ru.zikas1997.cacheupdater.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import ru.zikas1997.cacheupdater.model.MqMessage;
import ru.zikas1997.cacheupdater.service.MQListener;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class RabbitMQListenerImpl implements MQListener {

    private final CacheManager cacheManager;

    @Override
    @RabbitListener(queues = "${queue.cache}")
    public void listenCacheQueue(MqMessage mqMessage) {
        String[] caches = mqMessage.getNameCaches();
        if (caches != null && caches.length != 0) {
            for (String cache : caches) {
                Optional<Cache> optionalCache = Optional.ofNullable(cacheManager.getCache(cache));
                optionalCache.ifPresent(Cache::clear);
            }
        }
    }
}
