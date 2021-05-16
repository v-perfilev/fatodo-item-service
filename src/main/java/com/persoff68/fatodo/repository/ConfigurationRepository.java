package com.persoff68.fatodo.repository;

import com.mongodb.lang.NonNull;
import com.persoff68.fatodo.config.aop.cache.annotation.CacheEvictMethod;
import com.persoff68.fatodo.model.Configuration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfigurationRepository extends MongoRepository<Configuration, UUID> {

    List<Configuration> findAllByUserIdIn(List<UUID> userIds);

    Optional<Configuration> findByUserId(UUID userId);

    @Override
    @CacheEvictMethod(cacheName = "groups-by-user-id", key = "#configuration.userId")
    @NonNull
    <S extends Configuration> S save(@NonNull S configuration);

    @Override
    @CacheEvictMethod(cacheName = "groups-by-user-id", key = "#configuration.userId")
    void delete(@Nonnull Configuration configuration);

    @Override
    @CacheEvictMethod(cacheName = "groups-by-user-id", key = "#configurationList.userId")
    @NonNull
    <S extends Configuration> List<S> saveAll(@NonNull Iterable<S> configurationList);

    @Override
    @CacheEvictMethod(cacheName = "groups-by-user-id", key = "#configurationList.userId")
    void deleteAll(@NonNull Iterable<? extends Configuration> configurationList);
}
