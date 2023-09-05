package io.kontak.apps.anomaly.detector.config;

import io.kontak.apps.anomaly.detector.AnomalyDetector;
import io.kontak.apps.anomaly.detector.TemperatureMeasurementsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnomalyDetectorConfiguration {
    Logger logger = LoggerFactory.getLogger(AnomalyDetectorConfiguration.class);

    @Bean
    public TemperatureMeasurementsListener temperatureMeasurementsListener(
            final ConfigProperties configProperties,
            @Qualifier("anomalyDetectorAlgorithmOne") final AnomalyDetector anomalyDetectorOne,
            @Qualifier("anomalyDetectorAlgorithmTwo") final AnomalyDetector anomalyDetectorTwo,
            @Qualifier("alwaysAnomalyAnomalyDetector") final AnomalyDetector alwaysAnomalyDetector) {

        return new TemperatureMeasurementsListener(
                selectAnomalyDetector(configProperties, anomalyDetectorOne, anomalyDetectorTwo, alwaysAnomalyDetector)
        );
    }

    private AnomalyDetector selectAnomalyDetector(final ConfigProperties configProperties,
                                               final AnomalyDetector anomalyDetectorOne,
                                               final AnomalyDetector anomalyDetectorTwo,
                                               final AnomalyDetector alwaysAnomalyDetector) {
        String selectedAlgorithm = configProperties.getAlgorithm();
        logger.info("Selected algorithm: {}", selectedAlgorithm);
        if ("algorithmOne".equals(selectedAlgorithm)) {
            return anomalyDetectorOne;
        } else if ("algorithmTwo".equals(selectedAlgorithm)) {
            return anomalyDetectorTwo;
        } else if("alwaysAlgorithm".equals(selectedAlgorithm)) {
            return alwaysAnomalyDetector;
        } else {
            throw new IllegalArgumentException("Invalid Anomaly Detector implementation specified");
        }
    }

}
