package io.kontakt.apps.anomaly.storage.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ThermometerAnomalyCount {
    private String thermometerId;
    private int occurrences;
}
