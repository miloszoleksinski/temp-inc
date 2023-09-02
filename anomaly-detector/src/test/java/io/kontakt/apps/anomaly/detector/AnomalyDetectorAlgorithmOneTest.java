package io.kontakt.apps.anomaly.detector;

import io.kontak.apps.anomaly.detector.AnomalyDetectorAlgorithmOne;
import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnomalyDetectorAlgorithmOneTest {

    @Test
    public void testNoAnomalyDetected() {
        AnomalyDetectorAlgorithmOne algorithmOne = new AnomalyDetectorAlgorithmOne();

        List<TemperatureReading> readings = new ArrayList<>();
        for (double temperature : new double[]{20.1, 21.2, 20.3, 19.1, 20.1, 19.2, 20.1, 18.1, 19.4, 20.1, 23.1, 22.1}) {
            readings.add(new TemperatureReading(temperature, "roomId", "thermometerId", Instant.now()));
        }

        Optional<Anomaly> anomaly = algorithmOne.apply(readings);
        assertFalse(anomaly.isPresent(), "Anomaly should not be detected");
    }

    @Test
    public void testAnomalyDetected() {
        AnomalyDetectorAlgorithmOne algorithmOne = new AnomalyDetectorAlgorithmOne();

        List<TemperatureReading> readings = new ArrayList<>();
        for (double temperature : new double[]{20.1, 21.2, 20.3, 19.1, 20.1, 19.2, 20.1, 18.1, 19.4, 20.1, 27.1, 23.1}) {
            readings.add(new TemperatureReading(temperature, "roomId", "thermometerId", Instant.now()));
        }

        Optional<Anomaly> anomaly = algorithmOne.apply(readings);
        assertTrue(anomaly.isPresent(), "Anomaly should be detected");
    }

}
