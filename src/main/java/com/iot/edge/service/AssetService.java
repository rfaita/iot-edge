package com.iot.edge.service;

import com.iot.edge.component.LocalTokenCache;
import com.iot.edge.model.Asset;
import com.iot.edge.repository.AssetRepository;
import com.iot.edge.dto.SensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AssetService {

    private final static Logger LOGGER = LoggerFactory.getLogger(AssetService.class.getName());

    private final AssetRepository repository;
    private final LocalTokenCache cache;

    public AssetService(AssetRepository repository, LocalTokenCache cache) {
        this.repository = repository;
        this.cache = cache;
    }

    public boolean validateSensorData(SensorData sensorData) {

        String id = sensorData.getId();
        String tenantId = sensorData.getTenantId();
        String token = sensorData.getToken();

        if (!isPresentInCache(id, tenantId, token)) {

            LOGGER.info("Entry not found on cache: {},{},{}", id, tenantId, token);

            Optional<Asset> result =
                    repository.findByIdAndTenantIdAndToken(id, tenantId, token);

            if (result.isPresent()) {
                saveInCache(id, tenantId, token);
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } else {
            LOGGER.info("Entry found on cache: {},{},{}", id, tenantId, token);
            return Boolean.TRUE;
        }

    }

    private Boolean isPresentInCache(String id, String tenantId, String token) {
        return cache.validateSensorData(id, tenantId, token);
    }

    private void saveInCache(String id, String tenantId, String token) {
        cache.put(id, tenantId, token);
    }
}
