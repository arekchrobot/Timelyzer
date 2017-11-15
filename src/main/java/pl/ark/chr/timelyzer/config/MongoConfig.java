package pl.ark.chr.timelyzer.config;

import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.mongodb.morphia.Morphia;
import pl.ark.chr.timelyzer.util.AppProps;

import java.util.Collections;

public class MongoConfig {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private Morphia morphia;

    private static MongoConfig instance;

    private MongoConfig() {
        ServerAddress serverAddress = new ServerAddress(AppProps.instance().getDbMongoHost(), AppProps.instance().getDbMongoPort());
        ClusterSettings clusterSettings = ClusterSettings.builder().hosts(Collections.singletonList(serverAddress)).build();
        MongoClientSettings mongoSettings = MongoClientSettings.builder()
                .codecRegistry(com.mongodb.MongoClient.getDefaultCodecRegistry())
                .clusterSettings(clusterSettings).build();
        mongoClient = MongoClients.create(mongoSettings);
        mongoDatabase = mongoClient.getDatabase(AppProps.instance().getDbMongoDatabase());
        morphia = new Morphia().mapPackage("pl.ark.chr.timelyzer.persistence");
    }

    public static MongoConfig instance() {
        if(instance == null) {
            synchronized (MongoConfig.class) {
                if(instance == null) {
                    instance = new MongoConfig();
                }
            }
        }
        return instance;
    }

    public MongoDatabase mongoDatabase() {
        return mongoDatabase;
    }

    public Morphia morphia() {
        return morphia;
    }

    public void close() {
        mongoClient.close();
    }
}
