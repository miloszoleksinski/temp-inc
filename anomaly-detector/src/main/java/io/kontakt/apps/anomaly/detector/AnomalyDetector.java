package io.kontakt.apps.anomaly.detector;

import io.kontakt.apps.event.Anomaly;
import io.kontakt.apps.event.TemperatureReading;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface AnomalyDetector extends Function<List<TemperatureReading>, Optional<Anomaly>> {

}
