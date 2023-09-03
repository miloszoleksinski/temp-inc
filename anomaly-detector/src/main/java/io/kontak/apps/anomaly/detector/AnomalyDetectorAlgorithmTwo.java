package io.kontak.apps.anomaly.detector;

import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

@Component("anomalyDetectorAlgorithmTwo")
public class AnomalyDetectorAlgorithmTwo implements AnomalyDetector {

    private static final double ANOMALY_THRESHOLD = 5.0;
    private static final int WINDOW_SIZE_SECONDS = 10;
    private final Deque<TemperatureReading> readingBuffer = new ArrayDeque<>();

    @Override
    public Optional<Anomaly> apply(List<TemperatureReading> temperatureReadings) {
        Optional<Anomaly> anomaly = Optional.empty();

        for (TemperatureReading reading : temperatureReadings) {
            readingBuffer.addLast(reading);

            while (!readingBuffer.isEmpty() && !isWithinTimeWindow(readingBuffer.getFirst(), reading.timestamp())) {
                readingBuffer.removeFirst();
            }

            if (readingBuffer.size() >= 2) { // Minimum 2 readings needed to compare
                double averageTemperature = calculateAverageTemperature(readingBuffer);
                double currentTemperature = reading.temperature();
                if (isAnomaly(currentTemperature, averageTemperature)) {
                    anomaly = Optional.of(new Anomaly(reading));
                }
            }
        }
        return anomaly;
    }

    private boolean isWithinTimeWindow(final TemperatureReading reading, final Instant currentTime) {
        Instant readingTime = reading.timestamp();
        long timeDifferenceSeconds = Math.abs(readingTime.getEpochSecond() - currentTime.getEpochSecond());
        return timeDifferenceSeconds <= WINDOW_SIZE_SECONDS;
    }

    private double calculateAverageTemperature(final Deque<TemperatureReading> readings) {
        double sumOfTemperatures = readings.stream().mapToDouble(TemperatureReading::temperature).sum();
        return sumOfTemperatures / readings.size();
    }

    private boolean isAnomaly(final double currentTemperature, final double averageTemperature) {
        return Math.abs(currentTemperature - averageTemperature) > ANOMALY_THRESHOLD;
    }
}
