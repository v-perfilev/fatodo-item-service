package com.persoff68.fatodo.repository;

import com.mongodb.lang.NonNull;
import com.persoff68.fatodo.config.aop.cache.annotation.CacheEvictMethod;
import com.persoff68.fatodo.model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRepository extends MongoRepository<Group, UUID> {

    @Query(value = "{ 'members.id': ?0 }")
    List<Group> findAllByUserId(UUID userId);

    @Override
    @CacheEvictMethod(cacheName = "groups-by-id", key = "#group.id")
    @CacheEvictMethod(cacheName = "groups-by-members-id", key = "#group.members.id")
    @NonNull
    <S extends Group> S save(@NonNull S group);

    @Override
    @CacheEvictMethod(cacheName = "groups-by-id", key = "#group.id")
    @CacheEvictMethod(cacheName = "groups-by-members-id", key = "#group.members.id")
    void delete(@Nonnull Group group);

}