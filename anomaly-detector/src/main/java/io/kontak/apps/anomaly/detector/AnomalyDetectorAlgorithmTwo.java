package io.kontak.apps.anomaly.detector;

import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class AnomalyDetectorAlgorithmTwo implements AnomalyDetector {

    private static final double ANOMALY_THRESHOLD = 5.0;

    @Override
    public Optional<Anomaly> apply(List<TemperatureReading> temperatureReadings) {
        int numberOfReadings = temperatureReadings.size();
        for (int i = 0; i < numberOfReadings; i++) {
            if (i + 10 <= numberOfReadings) {
                List<TemperatureReading> consecutiveReadings = temperatureReadings.subList(i, i + 10);
                if (isWithinTimeWindow(consecutiveReadings) && isAnomaly(consecutiveReadings)) {
                    return Optional.of(new Anomaly(consecutiveReadings.get(9)));
                }
            }
        }
        return Optional.empty();
    }

    private boolean isWithinTimeWindow(List<TemperatureReading> readings) {
        Instant currentTimestamp = readings.get(9).timestamp();
        Instant oldestTimestamp = readings.get(0).timestamp();
        long timeDifferenceSeconds = currentTimestamp.minusSeconds(oldestTimestamp.getEpochSecond()).getEpochSecond();
        return timeDifferenceSeconds <= 10;
    }

    private boolean isAnomaly(List<TemperatureReading> readings) {
        double averageTemperature = calculateAverageTemperature(readings);
        TemperatureReading currentReading = readings.get(9);
        return Math.abs(currentReading.temperature() - averageTemperature) > ANOMALY_THRESHOLD;
    }

    private double calculateAverageTemperature(List<TemperatureReading> readings) {
        double sumOfTemperatures = readings.stream().mapToDouble(TemperatureReading::temperature).sum();
        return sumOfTemperatures / readings.size();
    }
}
