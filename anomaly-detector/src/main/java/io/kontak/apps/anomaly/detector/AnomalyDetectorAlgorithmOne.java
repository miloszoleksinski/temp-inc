package io.kontak.apps.anomaly.detector;

import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

@Component("anomalyDetectorAlgorithmOne")
public class AnomalyDetectorAlgorithmOne implements AnomalyDetector {
    Logger logger = LoggerFactory.getLogger(AnomalyDetectorAlgorithmOne.class);

    private static final double ANOMALY_THRESHOLD = 5.0;
    private static final int WINDOW_SIZE = 10;
    private final Deque<TemperatureReading> readingBuffer = new ArrayDeque<>(WINDOW_SIZE);

    @Override
    public Optional<Anomaly> apply(List<TemperatureReading> temperatureReadings) {
        Optional<Anomaly> anomaly = Optional.empty();

        for (TemperatureReading reading : temperatureReadings) {
            readingBuffer.addLast(reading);

            while (readingBuffer.size() > WINDOW_SIZE) {
                readingBuffer.removeFirst();
            }

            if (readingBuffer.size() == WINDOW_SIZE) {
                if (isAnomaly(readingBuffer)) {
                    anomaly = Optional.of(new Anomaly(reading));
                }
            }
        }
        return anomaly;
    }

    private boolean isAnomaly(final Deque<TemperatureReading> readings) {
        double sumOfFirstNineTemperatures = sumOfFirstNine(readings);
        double averageOfFirstNineTemperatures = sumOfFirstNineTemperatures / 9;
        TemperatureReading currentReading = readings.getLast();
        boolean isAnomaly = currentReading.temperature() > averageOfFirstNineTemperatures + ANOMALY_THRESHOLD;
        if(isAnomaly) {
            logger.info("Anomaly found, temperature: {}, average of 9 temperatures: {}, difference: {}",
                    currentReading.temperature(), averageOfFirstNineTemperatures, currentReading.temperature() - averageOfFirstNineTemperatures);
        }
        return isAnomaly;
    }

    private double sumOfFirstNine(final Deque<TemperatureReading> readings) {
        return readings.stream().limit(9).mapToDouble(TemperatureReading::temperature).sum();
    }
}
