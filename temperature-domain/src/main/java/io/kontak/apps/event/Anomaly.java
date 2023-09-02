package io.kontak.apps.event;

import java.time.Instant;

public record Anomaly(double temperature, String roomId, String thermometerId, Instant timestamp) {

    public Anomaly(final TemperatureReading tempReading) {
        this(tempReading.temperature(), tempReading.roomId(), tempReading.thermometerId(), tempReading.timestamp());
    }

}
