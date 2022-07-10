package com.persoff68.fatodo.repository;

import com.persoff68.fatodo.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, UUID> {

    List<Configuration> findAllByUserIdIn(List<UUID> userIds);

    Optional<Configuration> findByUserId(UUID userId);

}
