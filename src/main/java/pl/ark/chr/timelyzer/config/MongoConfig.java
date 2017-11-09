package pl.ark.chr.timelyzer.config;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import pl.ark.chr.timelyzer.util.ApplicationProperties;

public class MongoConfig {

    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;

    static {
        mongoClient = MongoClients.create(new ConnectionString(ApplicationProperties.getDbMongoConnection()));
        mongoDatabase = mongoClient.getDatabase(ApplicationProperties.getDbMongoDatabase());
    }

    public static MongoDatabase mongoDatabase() {
        return mongoDatabase;
    }

    public static void close() {
        mongoClient.close();
    }
}
