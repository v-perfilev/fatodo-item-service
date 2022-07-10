package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {

    @Query("""
            select g from Group g
            join Member m on g.id = m.group.id
            where m.userId = :userId and g.isDeleted = false
            """)
    List<Group> findAllByUserId(@Param("userId") UUID userId);

    @Query("""
            select g from Group g
            where g.id in :groupIds and g.isDeleted = false
            """)
    List<Group> findAllById(@Param("groupIds") List<UUID> groupIds);

    @Override
    @Query("""
            select g from Group g
            where g.id = :groupId and g.isDeleted = false
            """)
    Optional<Group> findById(@Param("groupId") UUID groupId);


}
