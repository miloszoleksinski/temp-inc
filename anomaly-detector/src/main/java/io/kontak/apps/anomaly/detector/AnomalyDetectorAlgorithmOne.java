package io.kontak.apps.anomaly.detector;

import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;

import java.util.List;
import java.util.Optional;

public class AnomalyDetectorAlgorithmOne implements AnomalyDetector {

    private static final double ANOMALY_THRESHOLD = 5.0;

    @Override
    public Optional<Anomaly> apply(List<TemperatureReading> temperatureReadings) {
        int numberOfReadings = temperatureReadings.size();
        for(int i=0; i < numberOfReadings; i++) {
            if (i + 10 <= numberOfReadings) {
                List<TemperatureReading> readings = temperatureReadings.subList(i, i + 10);
                boolean isAnomaly = isAnomaly(readings);
                if (isAnomaly) {
                    return Optional.of(new Anomaly(readings.get(9)));
                }
            }
        }
        return Optional.empty();
    }

    private boolean isAnomaly(final List<TemperatureReading> readings) {
        double sumOfFirstNineTemperatures = sumOfFirstNine(readings);
        double averageOfFirstNineTemperatures = sumOfFirstNineTemperatures / 9;
        TemperatureReading currentReading = readings.get(9);
        return currentReading.temperature() > averageOfFirstNineTemperatures + ANOMALY_THRESHOLD;
    }

    private double sumOfFirstNine(List<TemperatureReading> readings) {
        return readings.subList(0, 9).stream().mapToDouble(TemperatureReading::temperature).sum();
    }
}
