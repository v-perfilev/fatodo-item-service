package com.persoff68.fatodo.repository;

import com.mongodb.lang.NonNull;
import com.persoff68.fatodo.config.aop.cache.annotation.CacheEvictMethod;
import com.persoff68.fatodo.config.aop.cache.annotation.CacheableMethod;
import com.persoff68.fatodo.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends MongoRepository<Item, UUID> {

    @CacheableMethod(cacheName = "items-by-group-id", key = "#groupId")
    List<Item> findAllByGroupId(UUID groupId);

    @Override
    @CacheableMethod(cacheName = "item-by-id", key = "#id")
    @NonNull
    Optional<Item> findById(@NonNull UUID id);

    @Override
    @CacheEvictMethod(cacheName = "item-by-id", key = "#item.id")
    @CacheEvictMethod(cacheName = "items-by-group-id", key = "#item.groupId")
    @NonNull
    <S extends Item> S save(@NonNull S item);


    @Override
    @CacheEvictMethod(cacheName = "item-by-id", key = "#item.id")
    @CacheEvictMethod(cacheName = "items-by-group-id", key = "#item.groupId")
    void delete(@NonNull Item item);

    @CacheEvictMethod(cacheName = "item-by-id", key = "#id")
    @CacheEvictMethod(cacheName = "items-by-group-id", key = "#groupId")
    void deleteAllByIdInAndGroupId(List<UUID> id, UUID groupId);
}
