package com.assu.server.domain.map.repository;

import com.assu.server.domain.map.entity.Location;
import com.assu.server.domain.map.entity.enums.LocationOwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MapRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByOwnerTypeAndOwnerId(LocationOwnerType ownerType, Long ownerId);

    List<Location> findAllByOwnerTypeAndOwnerIdIn(LocationOwnerType ownerType, Collection<Long> ownerIds);

    @Query(value = """
        SELECT l.*
        FROM location l
        WHERE l.owner_type = :ownerType
            AND l.point IS NOT NULL
            AND ST_Contains(
                ST_GeomFromText(:wkt, 4326),
                l.point
            )
    """, nativeQuery = true)
    List<Location> findAllByCoordinates(
            @Param("ownerType") String ownerType,
            @Param("wkt") String wkt
    );
}
