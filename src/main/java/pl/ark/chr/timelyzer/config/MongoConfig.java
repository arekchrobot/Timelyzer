package pl.ark.chr.timelyzer.config;

import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.mongodb.morphia.Morphia;
import pl.ark.chr.timelyzer.util.ApplicationProperties;

import java.util.Collections;

public class MongoConfig {

    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;
    private static Morphia morphia;

    static {
        ServerAddress serverAddress = new ServerAddress(ApplicationProperties.getDbMongoHost(), ApplicationProperties.getDbMongoPort());
        ClusterSettings clusterSettings = ClusterSettings.builder().hosts(Collections.singletonList(serverAddress)).build();
        MongoClientSettings mongoSettings = MongoClientSettings.builder()
                .codecRegistry(com.mongodb.MongoClient.getDefaultCodecRegistry())
                .clusterSettings(clusterSettings).build();
        mongoClient = MongoClients.create(mongoSettings);
        mongoDatabase = mongoClient.getDatabase(ApplicationProperties.getDbMongoDatabase());
        morphia = new Morphia().mapPackage("pl.ark.chr.timelyzer.persistence");
    }

    public static MongoDatabase mongoDatabase() {
        return mongoDatabase;
    }

    public static Morphia morphia() {
        return morphia;
    }

    public static void close() {
        mongoClient.close();
    }
}
