package com.persoff68.fatodo.repository;

import com.mongodb.lang.NonNull;
import com.persoff68.fatodo.config.aop.cache.annotation.CacheEvictMethod;
import com.persoff68.fatodo.config.aop.cache.annotation.CacheableMethod;
import com.persoff68.fatodo.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {

    @CacheableMethod(cacheName = "items-by-group-id", key = "#groupId")
    List<Item> findAllByGroupId(String groupId);

    @Override
    @CacheableMethod(cacheName = "items-by-id", key = "#id")
    @NonNull
    Optional<Item> findById(@NonNull String id);

    @Override
    @CacheEvictMethod(cacheName = "items-by-id", key = "#item.id")
    @CacheEvictMethod(cacheName = "items-by-group-id", key = "#item.groupId")
    @NonNull
    <S extends Item> S save(@NonNull S item);


    @Override
    @CacheEvictMethod(cacheName = "items-by-id", key = "#item.id")
    @CacheEvictMethod(cacheName = "items-by-group-id", key = "#item.groupId")
    void delete(@NonNull Item item);

    @CacheEvictMethod(cacheName = "items-by-id", key = "#id")
    @CacheEvictMethod(cacheName = "items-by-group-id", key = "#groupId")
    void deleteAllByIdInAndGroupId(List<String> id, String groupId);
}
