package booking.tranaction.check.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@TestConfiguration
public class TestConfig {
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017/test");
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(mongoClient(), "test");
    }
}
