package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

    @Query("""
            select i from Item i
            join Group g on i.group.id = g.id
            where g.id = :groupId and g.isDeleted = false and i.isDeleted = false and i.isArchived = false
            order by g.createdAt desc
            """)
    Page<Item> findAllByGroupIdPageable(@Param("groupId") UUID groupId, Pageable pageable);

    @Query("""
            select i from Item i
            join Group g on i.group.id = g.id
            where g.id = :groupId and g.isDeleted = false and i.isDeleted = false and i.isArchived = true 
            order by g.createdAt desc
            """)
    Page<Item> findAllArchivedByGroupIdPageable(@Param("groupId") UUID groupId, Pageable pageable);

    @Query("""
            select i from Item i
            join Group g on i.group.id = g.id
            where g.id = :groupId and g.isDeleted = false and i.isDeleted = false
            """)
    List<Item> findAllByGroupId(@Param("groupId") UUID groupId);

    @Query("""
            select i from Item i
            join Group g on i.group.id = g.id
            where i.id in :itemIds and g.isDeleted = false and i.isDeleted = false
            """)
    List<Item> findAllByIds(@Param("itemIds") List<UUID> itemIds);

    @Override
    @Query("""
            select i from Item i
            join Group g on i.group.id = g.id
            where i.id = :itemId and g.isDeleted = false and i.isDeleted = false
            """)
    Optional<Item> findById(@Param("itemId") UUID id);

}
