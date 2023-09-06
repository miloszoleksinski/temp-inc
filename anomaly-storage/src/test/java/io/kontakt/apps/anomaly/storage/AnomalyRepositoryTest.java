package io.kontakt.apps.anomaly.storage;

import io.kontakt.apps.anomaly.storage.model.AnomalyDB;
import io.kontakt.apps.anomaly.storage.model.ThermometerAnomalyCount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AnomalyRepositoryTest extends AbstractIntegrationTest {
    @Autowired
    private AnomalyRepository anomalyRepository;
    @Autowired
    private AnomalyService anomalyService;

    @Test
    void existingAnomalyTest() {
        String id = "existing_anomaly_1";
        AnomalyDB AnomalyDB = new AnomalyDB(id, 25d, "existing_room_1", "existing_anomaly_1", Instant.now());
        anomalyRepository.save(AnomalyDB);
        assertNotNull(anomalyService.findById(id));
    }

    @Test
    void existingAnomalyThresholdTest() {
        for(int i=0; i<=5; i++) {
            AnomalyDB AnomalyDB = new AnomalyDB(String.format("existing_anomaly_%s", i), 25d, "existing_room_1", "existing_anomaly_1", Instant.now());
            anomalyRepository.save(AnomalyDB);
        }
        List<ThermometerAnomalyCount> anomalies = anomalyService.findThermometersWithMoreAnomaliesThanThreshold(5);
        assertFalse(anomalies.isEmpty());
    }

    @Test
    void nonExistingAnomalyTest() {
        assertNull(anomalyService.findById("non_existing_anomaly_1"));
    }

}
