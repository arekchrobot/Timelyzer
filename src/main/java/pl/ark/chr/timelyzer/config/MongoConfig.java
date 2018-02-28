package pl.ark.chr.timelyzer.config;

import com.mongodb.DBObjectCodec;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.mongodb.morphia.Morphia;
import pl.ark.chr.timelyzer.config.codecs.LocalDateCodec;
import pl.ark.chr.timelyzer.config.converters.LocalDateConverter;
import pl.ark.chr.timelyzer.util.AppProps;

import java.util.Collections;

public class MongoConfig {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private Morphia morphia;
    private CodecRegistry codecRegistry;

    private MongoConfig() {
        ServerAddress serverAddress = new ServerAddress(AppProps.instance().getDbMongoHost(), AppProps.instance().getDbMongoPort());
        ClusterSettings clusterSettings = ClusterSettings.builder().hosts(Collections.singletonList(serverAddress)).build();
        MongoClientSettings mongoSettings = MongoClientSettings.builder()
                .codecRegistry(com.mongodb.MongoClient.getDefaultCodecRegistry())
                .clusterSettings(clusterSettings).build();
        mongoClient = MongoClients.create(mongoSettings);
        mongoDatabase = mongoClient.getDatabase(AppProps.instance().getDbMongoDatabase());
        morphia = new Morphia().mapPackage("pl.ark.chr.timelyzer.persistence");
        morphia.getMapper().getConverters().addConverter(new LocalDateConverter());
        DBObjectCodec dbObjectCodec = new DBObjectCodec(CodecRegistries.fromRegistries(MongoClients.getDefaultCodecRegistry(), CodecRegistries.fromCodecs(new LocalDateCodec())));
        codecRegistry = CodecRegistries.fromRegistries(MongoClients.getDefaultCodecRegistry(), CodecRegistries.fromCodecs(new LocalDateCodec(), dbObjectCodec));
    }

    private static class SingletonHelper {
        private static final MongoConfig INSTANCE = new MongoConfig();
    }

    public static MongoConfig instance() {
        return SingletonHelper.INSTANCE;
    }

    public MongoDatabase mongoDatabase() {
        return mongoDatabase.withCodecRegistry(codecRegistry);
    }

    public Morphia morphia() {
        return morphia;
    }

    public void close() {
        mongoClient.close();
    }
}
