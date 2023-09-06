package io.kontakt.apps.anomaly.storage;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(classes = AnomalyStorageApplication.class)
@Testcontainers
@EnableMongoRepositories(basePackages = "io.kontakt.apps.anomaly.storage")
public class AbstractIntegrationTest {

    @Container
    public final static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest")).withExposedPorts(27017);

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        mongoDBContainer.start();
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

}
