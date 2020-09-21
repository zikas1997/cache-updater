package ru.zikas1997.cacheupdater.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Аннотация ставится над классом, который содержит методы обновления кэша
 * */
@Retention(RetentionPolicy.RUNTIME)
public @interface TriggerCache {
}
