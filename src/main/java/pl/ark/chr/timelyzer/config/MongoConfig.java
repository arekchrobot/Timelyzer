package pl.ark.chr.timelyzer.config;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.mongodb.morphia.Morphia;
import pl.ark.chr.timelyzer.util.ApplicationProperties;

public class MongoConfig {

    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;
    private static Morphia morphia;

    static {
        System.out.println(ApplicationProperties.getDbMongoConnection());
        mongoClient = MongoClients.create(new ConnectionString(ApplicationProperties.getDbMongoConnection()));
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
