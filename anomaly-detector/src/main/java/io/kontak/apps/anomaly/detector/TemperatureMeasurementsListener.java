package io.kontak.apps.anomaly.detector;

import io.kontak.anomaly.storage.AnomalyService;
import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import org.apache.kafka.streams.kstream.KStream;

import java.util.List;
import java.util.function.Function;

public class TemperatureMeasurementsListener implements Function<KStream<String, TemperatureReading>, KStream<String, Anomaly>> {
    private final AnomalyDetector anomalyDetector;
    private final AnomalyService anomalyService;

    public TemperatureMeasurementsListener(final AnomalyDetector anomalyDetector, final AnomalyService anomalyService) {
        this.anomalyDetector = anomalyDetector;
        this.anomalyService = anomalyService;
    }

    @Override
    public KStream<String, Anomaly> apply(KStream<String, TemperatureReading> events) {
        return events
                .mapValues(temperatureReading -> anomalyDetector.apply(List.of(temperatureReading)))
                .filter((s, anomaly) -> anomaly.isPresent())
                .mapValues((s, anomaly) -> {
                    Anomaly detectedAnomaly = anomaly.get();
                    anomalyService.save(detectedAnomaly);
                    return detectedAnomaly;
                })
                .selectKey((s, anomaly) -> anomaly.thermometerId());
    }
}
