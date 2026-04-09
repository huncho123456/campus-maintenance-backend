package com.praise.incident.repo;

import com.praise.incident.entity.IncidentEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IncidentRepo extends CrudRepository <IncidentEntity, UUID>{

    Optional<IncidentEntity> findById(UUID uuid);
    List<IncidentEntity> findByUserEmail(String email);
    Optional<IncidentEntity> findByIncidentNumber(String incidentNumber);
}
