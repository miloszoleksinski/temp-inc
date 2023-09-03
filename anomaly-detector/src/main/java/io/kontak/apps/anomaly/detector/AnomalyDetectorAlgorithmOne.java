package io.kontak.apps.anomaly.detector;

import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

@Component("anomalyDetectorAlgorithmOne")
public class AnomalyDetectorAlgorithmOne implements AnomalyDetector {

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
                System.out.println("reading bufer size equals windows size");
                if (isAnomaly(readingBuffer)) {
                    System.out.println("we have found an anomaly, temperature: " + reading.temperature() );
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
        return currentReading.temperature() > averageOfFirstNineTemperatures + ANOMALY_THRESHOLD;
    }

    private double sumOfFirstNine(final Deque<TemperatureReading> readings) {
        return readings.stream().limit(9).mapToDouble(TemperatureReading::temperature).sum();
    }
}
