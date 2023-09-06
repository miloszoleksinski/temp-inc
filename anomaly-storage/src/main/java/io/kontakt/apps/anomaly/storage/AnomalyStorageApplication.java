package io.kontakt.apps.anomaly.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "io.kontak.anomaly.storage")
public class AnomalyStorageApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnomalyStorageApplication.class, args);
    }
}