package com.persoff68.fatodo.config.aop.cache;

import com.persoff68.fatodo.config.aop.cache.annotation.CacheEvictMethod;
import com.persoff68.fatodo.config.aop.cache.annotation.CacheableMethod;
import com.persoff68.fatodo.config.aop.cache.util.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CacheAspect {

    private final CacheManager cacheManager;

    @Around("@annotation(cacheableMethod)")
    public Object doCustomCacheable(ProceedingJoinPoint pjp, CacheableMethod cacheableMethod) throws Throwable {
        Cache cache = cacheManager.getCache(cacheableMethod.cacheName());
        Object key = getKey(pjp, cacheableMethod.key());
        if (cache != null) {
            Object object = cache.get(key, getReturnType(pjp));
            if (object != null) {
                log.debug("Read from cache: {} - {}", cacheableMethod.cacheName(), cacheableMethod.key());
                return object;
            }
        }
        Object result = pjp.proceed();
        if (cache != null) {
            log.debug("Write to cache: {} - {}", cacheableMethod.cacheName(), cacheableMethod.key());
            cache.put(key, result);
        }
        return result;
    }

    @Around("@annotation(cacheEvictMethod)")
    public Object doCustomCacheEvict(ProceedingJoinPoint pjp, CacheEvictMethod cacheEvictMethod) throws Throwable {
        Cache cache = cacheManager.getCache(cacheEvictMethod.cacheName());
        Object result = pjp.proceed();
        if (cache != null) {
            Collection<?> keyCollection = getKeyCollection(pjp, cacheEvictMethod.key());
            for (Object key : keyCollection) {
                log.debug("Delete from cache: {} - {}", cacheEvictMethod.cacheName(), key);
                cache.evict(key);
            }
        }
        return result;
    }

    private static Class<?> getReturnType(ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        return methodSignature.getReturnType();
    }

    private static Object getKey(ProceedingJoinPoint pjp, String key) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        String[] names = methodSignature.getParameterNames();
        Object[] args = pjp.getArgs();
        return CacheUtils.getValue(names, args, key);
    }

    private static Collection<?> getKeyCollection(ProceedingJoinPoint pjp, String key) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        String[] names = methodSignature.getParameterNames();
        Object[] args = pjp.getArgs();
        return CacheUtils.getCollectionValue(names, args, key);
    }

}
