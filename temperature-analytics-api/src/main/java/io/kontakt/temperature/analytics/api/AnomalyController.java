package io.kontakt.temperature.analytics.api;

import io.kontakt.apps.anomaly.storage.AnomalyService;
import io.kontakt.apps.anomaly.storage.model.ThermometerAnomalyCount;
import io.kontakt.apps.event.Anomaly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AnomalyController {
    private static final Logger logger = LoggerFactory.getLogger(AnomalyController.class);
    private final AnomalyService anomalyService;

    public AnomalyController(final AnomalyService anomalyService) {
        this.anomalyService = anomalyService;
    }

    @GetMapping("/anomalies")
    private ResponseEntity<List<Anomaly>> anomaliesMainPage() {
        logger.info("Getting all anomalies");
        List<Anomaly> anomalies = anomalyService.findAll();
        return ResponseEntity.ok(anomalies);
    }

    @GetMapping("/anomalies/{thermometerId}")
    private ResponseEntity<List<Anomaly>> getAnomaliesByThermometerId(@PathVariable String thermometerId) {
        logger.info("Getting anomalies by thermometerId: {}", thermometerId);
        List<Anomaly> anomalies = anomalyService.findByThermometerId(thermometerId);
        return ResponseEntity.ok(anomalies);
    }

    @GetMapping("/anomalies/room/{roomId}")
    private ResponseEntity<List<Anomaly>> getAnomaliesByRoomId(@PathVariable String roomId) {
        logger.info("Getting anomalies by roomId: {}", roomId);
        List<Anomaly> anomalies = anomalyService.findByRoomId(roomId);
        return ResponseEntity.ok(anomalies);
    }

    @GetMapping("/anomalies/threshold/{threshold}")
    private ResponseEntity<List<ThermometerAnomalyCount>> getThermometersWithMoreAnomaliesThanThreshold(@PathVariable int threshold) {
        logger.info("Getting thermometers with more anomalies than threshold: {}", threshold);
        List<ThermometerAnomalyCount> anomalies = anomalyService.findThermometersWithMoreAnomaliesThanThreshold(threshold);
        return ResponseEntity.ok(anomalies);
    }
}
