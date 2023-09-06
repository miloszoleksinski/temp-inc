package io.kontakt.apps.temperature.generator;

import io.kontakt.apps.event.TemperatureReading;

import java.util.List;

public interface TemperatureGenerator {

    List<TemperatureReading> generate();
}
