package io.kontak.anomaly.storage;

import io.kontak.anomaly.storage.model.AnomalyDB;
import io.kontak.anomaly.storage.model.ThermometerAnomalyCount;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnomalyRepository extends MongoRepository<AnomalyDB, String> {
    List<AnomalyDB> findByThermometerId(final String thermometerId);
    List<AnomalyDB> findByRoomId(final String roomId);
    @Aggregation(pipeline = {
            "{ $group: { _id: '$thermometerId', count: { $sum: 1 } } }",
            "{ $match: { count: { $gt: :#{#threshold} } } }",
            "{ $project: { _id: 0, thermometerId: '$_id', occurrences: '$count' } }"
    })
    List<ThermometerAnomalyCount> findThermometersWithMoreAnomaliesThanThreshold(@Param("threshold") int threshold);

}
