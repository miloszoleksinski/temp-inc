package io.kontak.apps.temperature.generator;

import io.kontak.apps.event.TemperatureReading;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SimpleTemperatureGenerator implements TemperatureGenerator {

    private final Random random = new Random();
    private final Map<String, String> thermometerRoomMapping = new ConcurrentHashMap<>();

    public SimpleTemperatureGenerator() {
        thermometerRoomMapping.put("Thermometer1-1", "Room1");
        thermometerRoomMapping.put("Thermometer2-1", "Room2");
        thermometerRoomMapping.put("Thermometer2-2", "Room2");
        thermometerRoomMapping.put("Thermometer2-3", "Room2");
        thermometerRoomMapping.put("Thermometer3-1", "Room3");
    }

    @Override
    public List<TemperatureReading> generate() {
        List<TemperatureReading> temperatureReadings = new ArrayList<>();
        for(int i=0; i<=1; i++) {
            temperatureReadings.add(generateSingleReading());
        }
        return temperatureReadings;
    }

    private TemperatureReading generateSingleReading() {
        double temperature = random.nextDouble(10d, 30d);
        String thermometerId = getRandomThermometer();
        String roomId = thermometerRoomMapping.get(thermometerId);
        return new TemperatureReading(temperature, roomId, thermometerId, Instant.now());
    }

    private String getRandomThermometer() {
        int index = random.nextInt(thermometerRoomMapping.size());
        return thermometerRoomMapping.keySet().toArray(new String[0])[index];
    }
}
