package com.assu.server.global.config;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaSpatialConfig {
    @Bean
    public GeometryFactory geometryFactory() {
        // PrecisionModel 기본, SRID=4326 (WGS84)
        return new GeometryFactory(new PrecisionModel(), 4326);
    }
}
