package com.persoff68.fatodo.config.aop.cache;

import com.persoff68.fatodo.config.aop.cache.annotation.CustomCacheEvict;
import com.persoff68.fatodo.config.aop.cache.annotation.CustomCacheable;
import com.persoff68.fatodo.config.aop.cache.annotation.CustomListCacheEvict;
import com.persoff68.fatodo.config.aop.cache.annotation.CustomListCacheable;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unchecked")
public class CacheAspect {

    private final CacheManager cacheManager;

    @Around("@annotation(customCacheable)")
    public Object doCustomCacheable(ProceedingJoinPoint pjp, CustomCacheable customCacheable) throws Throwable {
        Cache cache = cacheManager.getCache(customCacheable.cacheName());
        Object key = getKey(pjp, customCacheable.key());
        if (cache != null) {
            Object object = cache.get(key, getReturnType(pjp));
            if (object != null) {
                log.debug("Read from cache: {} - {}", customCacheable.cacheName(), customCacheable.key());
                return object;
            }
        }
        Object result = pjp.proceed();
        if (cache != null) {
            log.debug("Write to cache: {} - {}", customCacheable.cacheName(), customCacheable.key());
            cache.put(key, result);
        }
        return result;
    }

    @Around("@annotation(customCacheEvict)")
    public Object doCustomCacheEvict(ProceedingJoinPoint pjp, CustomCacheEvict customCacheEvict) throws Throwable {
        Cache cache = cacheManager.getCache(customCacheEvict.cacheName());
        Object result = pjp.proceed();
        if (cache != null) {
            Collection<?> keyCollection = getKeyCollection(pjp, customCacheEvict.key());
            for (Object key : keyCollection) {
                log.debug("Delete from cache: {} - {}", customCacheEvict.cacheName(), key);
                cache.evict(key);
            }
        }
        return result;
    }

    @Around("@annotation(customListCacheable)")
    public Object doCustomListCacheable(ProceedingJoinPoint pjp, CustomListCacheable customListCacheable)
            throws Throwable {
        Cache cache = cacheManager.getCache(customListCacheable.cacheName());

        List<?> sortedList = getKeyCollection(pjp, customListCacheable.key())
                .stream().sorted().collect(Collectors.toList());
        int hash = sortedList.hashCode();

        if (cache != null) {
            Object object = cache.get(hash, getReturnType(pjp));
            if (object != null) {
                log.debug("Read from cache: {} - {}", customListCacheable.cacheName(), hash);
                return object;
            }
        }

        Cache keyCache = cacheManager.getCache(customListCacheable.keyCacheName());
        Object result = pjp.proceed();

        if (cache != null && keyCache != null) {
            log.debug("Write to cache: {} - {}", customListCacheable.cacheName(), hash);
            cache.put(hash, result);

            sortedList.forEach(key -> {
                List<Integer> keyHashList = keyCache.get(key, List.class);
                if (keyHashList == null) {
                    keyHashList = new ArrayList<>();
                }
                keyHashList.add(hash);
                log.debug("Write to key cache: {} - {}", customListCacheable.keyCacheName(), key);
                keyCache.put(key, keyHashList);
            });
        }

        return result;
    }

    @Around("@annotation(customCacheEvict)")
    public Object doCustomListCacheEvict(ProceedingJoinPoint pjp, CustomListCacheEvict customCacheEvict)
            throws Throwable {
        Cache cache = cacheManager.getCache(customCacheEvict.cacheName());
        Cache keyCache = cacheManager.getCache(customCacheEvict.keyCacheName());
        Object result = pjp.proceed();
        if (cache != null && keyCache != null) {
            Collection<?> keyCollection = getKeyCollection(pjp, customCacheEvict.key());
            for (Object key : keyCollection) {
                List<Integer> keyHashList = keyCache.get(key, List.class);
                if (keyHashList != null) {
                    keyHashList.forEach(hash -> {
                        log.debug("Delete from cache: {} - {}", customCacheEvict.cacheName(), hash);
                        cache.evict(hash);
                    });
                    log.debug("Delete from key cache: {} - {}", customCacheEvict.keyCacheName(), key);
                    keyCache.evict(key);
                }
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
