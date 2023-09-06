package io.kontakt.apps.anomaly.storage.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "anomalies")
public class AnomalyDB {
    @Id
    private String id;
    private double temperature;
    private String roomId;
    private String thermometerId;
    private Instant timestamp;
}
