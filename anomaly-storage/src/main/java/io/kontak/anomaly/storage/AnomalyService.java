package io.kontak.anomaly.storage;

import io.kontak.anomaly.storage.exception.AnomalyServiceException;
import io.kontak.anomaly.storage.mapper.AnomalyMapper;
import io.kontak.anomaly.storage.model.AnomalyDB;
import io.kontak.anomaly.storage.model.ThermometerAnomalyCount;
import io.kontak.apps.event.Anomaly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class AnomalyService {
    private final AnomalyRepository anomalyRepository;
    private final AnomalyMapper anomalyMapper;
    private static final Logger logger = LoggerFactory.getLogger(AnomalyService.class);

    public AnomalyService(final AnomalyRepository anomalyRepository, final AnomalyMapper anomalyMapper) {
        this.anomalyRepository = anomalyRepository;
        this.anomalyMapper = anomalyMapper;
    }

    public void save(final Anomaly detectedAnomaly) {
        AnomalyDB anomalyDB = anomalyMapper.toDTO(detectedAnomaly);
        handleExceptionAndLog(() -> anomalyMapper.toModel(anomalyRepository.save(anomalyDB)), "save anomaly");
    }

    public List<Anomaly> findAll() {
        anomalyRepository.findAll().forEach(x -> System.out.println("1 " + x.getThermometerId()));
        List<AnomalyDB> anomalyDBList = handleExceptionAndLog(anomalyRepository::findAll,"retrieve all anomalies");
        anomalyDBList.forEach(x -> System.out.println(x.getThermometerId()));
        return anomalyDBList.stream().map(anomalyMapper::toModel).collect(Collectors.toList());
    }

    public List<Anomaly> findByThermometerId(final String thermometerId) {
        List<AnomalyDB> anomalyDBList = handleExceptionAndLog(() -> anomalyRepository.findByThermometerId(thermometerId),
                "retrieve anomalies by thermometerId");
        return anomalyDBList.stream().map(anomalyMapper::toModel).collect(Collectors.toList());
    }

    public List<Anomaly> findByRoomId(final String roomId) {
        List<AnomalyDB> anomalyDBList = handleExceptionAndLog(() -> anomalyRepository.findByRoomId(roomId),
                "retrieve anomalies by roomId");
        return anomalyDBList.stream().map(anomalyMapper::toModel).collect(Collectors.toList());
    }

    public List<ThermometerAnomalyCount> findThermometersWithMoreAnomaliesThanThreshold(final int threshold) {
        return handleExceptionAndLog(() -> anomalyRepository.findThermometersWithMoreAnomaliesThanThreshold(threshold),
                String.format("retrieve thermometers with more anomalies than %s", threshold));
    }

    private <T> T handleExceptionAndLog(final Supplier<T> action, final String actionDescription) {
        try {
            return action.get();
        } catch (Exception e) {
            logger.error("Failed to {} : {}", actionDescription, e.getMessage());
            throw new AnomalyServiceException(String.format("Failed to %s :", actionDescription), e);
        }
    }
}
