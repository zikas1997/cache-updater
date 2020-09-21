package ru.zikas1997.cacheupdater.service;

import ru.zikas1997.cacheupdater.model.MqMessage;

public interface MQListener {
    void listenCacheQueue(MqMessage mqMessage);
}
