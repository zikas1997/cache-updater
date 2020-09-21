package ru.zikas1997.cacheupdater.bpp;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cglib.proxy.Proxy;
import ru.zikas1997.cacheupdater.annotation.TriggerCache;
import ru.zikas1997.cacheupdater.model.MqMessage;
import ru.zikas1997.cacheupdater.service.MQSender;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс отслеживает бины с аннотацией {@link TriggerCache}, модифицируя методы с аннтоацией {@link CacheEvict}.
 * */
public class CacheBeanPostProcessor implements BeanPostProcessor {

    Map<String, BeanWithTriggerCache> map = new LinkedHashMap<>();
    private MQSender mqSender;

    public CacheBeanPostProcessor(MQSender mqSender) {
        this.mqSender = mqSender;
    }

    /**
     * Отслеживаем бины с аннотацией {@link CacheEvict},
     * собираем основную ифнормацию по методу и складываем в  map
     * */
    @Override
    public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {
        Class<?> aClass = bean.getClass();
        if (aClass.isAnnotationPresent(TriggerCache.class)) {
            List<MethodCache> methodCaches = new ArrayList<>();
            for (Method declaredMethod : aClass.getDeclaredMethods()) {
                CacheEvict annotation = declaredMethod.getAnnotation(CacheEvict.class);
                if(annotation!=null){
                    methodCaches.add(new MethodCache(declaredMethod.getName(), declaredMethod.getParameterTypes(), annotation.value()));
                }
            }
            BeanWithTriggerCache beanWithTriggerCache = new BeanWithTriggerCache(bean, methodCaches);
            map.put(beanName, beanWithTriggerCache);
        }
        return bean;
    }

    /**
     * Все бины из map сверяются с прокси методом. В случаи совпадения методов, названия кэша отправится в exchange:
     * "exchange-cache" где дальше распределится во все очереди.
     * */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        BeanWithTriggerCache beanWithTriggerCache = map.get(beanName);
        if (beanWithTriggerCache != null) {
            Class<?> beanClass = beanWithTriggerCache.getBean().getClass();
            return Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), (proxy1, method, args) -> {
                if (!beanWithTriggerCache.getMethodCaches().isEmpty()) {
                    for (MethodCache methodCache : beanWithTriggerCache.getMethodCaches()) {
                        if (methodCache.getName().equals(method.getName()) && equalsParams(methodCache.getParams(), method.getParameterTypes())) {
                            mqSender.send(new MqMessage(methodCache.getNameCaches()));
                        }
                    }
                }
                return method.invoke(bean, args);
            });
        } else {
            return bean;
        }
    }

    /**
     * Фукция срафнивает 2 фукции по параметрам:
     * 1. название ф-ции
     * 2. типы аргументов
     * */
    private boolean equalsParams(Class<?>[] nameCaches, Class<?>[] parameterTypes) {
        if (nameCaches.length == parameterTypes.length)
            for (int i = 0; i < nameCaches.length; i++) {
                if (nameCaches[i] != parameterTypes[i]) {
                    return false;
                }
            }
        return true;
    }

    @Data
    @AllArgsConstructor
    class BeanWithTriggerCache {
        Object bean;
        List<MethodCache> methodCaches;
    }

    @Data
    @AllArgsConstructor
    class MethodCache {
        String name;
        Class<?>[] params;
        String[] nameCaches;
    }
}
