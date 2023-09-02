package io.kontakt.apps.anomaly.detector;

import io.kontak.apps.anomaly.detector.AnomalyDetectorAlgorithmTwo;
import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnomalyDetectorAlgorithmTwoTest {

    @Test
    public void testNoAnomalyDetected() {
        AnomalyDetectorAlgorithmTwo algorithmTwo = new AnomalyDetectorAlgorithmTwo();

        List<TemperatureReading> readings = new ArrayList<>();
        for (double temperature : new double[]{19.1, 19.2, 19.5, 19.7, 19.3, 25.1, 18.2, 19.1, 19.2, 25.4}) {
            readings.add(new TemperatureReading(temperature, "roomId", "thermometerId", Instant.now()));
        }

        Optional<Anomaly> anomaly = algorithmTwo.apply(readings);
        assertFalse(anomaly.isPresent(), "Anomaly should not be detected");
    }

    @Test
    public void testAnomalyDetected() {
        AnomalyDetectorAlgorithmTwo algorithmTwo = new AnomalyDetectorAlgorithmTwo();

        List<TemperatureReading> readings = new ArrayList<>();
        for (double temperature : new double[]{19.1, 19.2, 19.5, 19.7, 19.3, 25.1, 18.2, 19.1, 19.2, 30.4}) {
            readings.add(new TemperatureReading(temperature, "roomId", "thermometerId", Instant.now()));
        }

        Optional<Anomaly> anomaly = algorithmTwo.apply(readings);
        assertTrue(anomaly.isPresent(), "Anomaly should be detected");
    }

}
