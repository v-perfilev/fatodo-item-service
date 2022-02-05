package com.persoff68.fatodo.repository;

import com.mongodb.lang.NonNull;
import com.persoff68.fatodo.config.aop.cache.annotation.CacheEvictMethod;
import com.persoff68.fatodo.config.aop.cache.annotation.CacheableMethod;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.web.rest.ItemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends MongoRepository<Item, UUID> {

    @Query(value = "{ 'groupId' : ?0, 'archived': false }", sort = "{ 'createdAt' : -1}")
    @CacheableMethod(cacheName = "items-by-group-id", key = "#groupId")
    Page<Item> findDefaultByGroupIdPageable(UUID groupId, Pageable pageable);

    @Query(value = "{ 'groupId' : ?0, 'archived': false }", sort = "{ 'createdAt' : -1}")
    Page<Item> findOtherByGroupIdPageable(UUID groupId, Pageable pageable);

    @Query(value = "{ 'groupId' : ?0, 'archived': true }", sort = "{ 'createdAt' : -1}")
    Page<Item> findArchivedByGroupIdPageable(UUID groupId, Pageable pageable);

    @Query(value = "{ 'groupId' : ?0 }")
    List<Item> findAllByGroupId(UUID groupId);

    @Query(value = "{'id' : {'$in' : ?0}}")
    List<Item> findAllByIds(List<UUID> itemIdList);

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

    default Page<Item> findByGroupIdPageable(UUID groupId, Pageable pageable) {
        if (pageable.getPageSize() == ItemResource.DEFAULT_ITEMS_LENGTH
                && pageable.getOffset() == 0 && pageable.getPageNumber() == 0) {
            return this.findDefaultByGroupIdPageable(groupId, pageable);
        } else {
            return this.findOtherByGroupIdPageable(groupId, pageable);
        }
    }
}
