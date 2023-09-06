package io.kontakt.apps.anomaly.detector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"io.kontak.anomaly.storage", "io.kontak.apps.anomaly.detector"})
public class AnomalyDetectorApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnomalyDetectorApplication.class, args);
    }
}
