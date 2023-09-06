package io.kontak.anomaly.storage.mapper;

import io.kontak.anomaly.storage.model.AnomalyDB;
import io.kontak.apps.event.Anomaly;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AnomalyMapper implements Mapper<Anomaly, AnomalyDB> {

    @Override
    public AnomalyDB toDTO(Anomaly anomaly) {
        AnomalyDB anomalyDB = new AnomalyDB();
        anomalyDB.setTemperature(anomaly.temperature());
        anomalyDB.setThermometerId(anomaly.thermometerId());
        anomalyDB.setRoomId(anomaly.roomId());
        anomalyDB.setTimestamp(anomaly.timestamp());
        return anomalyDB;
    }

    @Override
    public Anomaly toModel(AnomalyDB anomalyDB) {
        double temperature = anomalyDB.getTemperature();
        String thermometerId = anomalyDB.getThermometerId();
        String roomId = anomalyDB.getRoomId();
        Instant timestamp = anomalyDB.getTimestamp();
        return new Anomaly(temperature, roomId, thermometerId, timestamp);
    }
}
