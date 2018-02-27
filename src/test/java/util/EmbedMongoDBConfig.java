package util;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.Timeout;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import pl.ark.chr.timelyzer.util.AppProps;

public class EmbedMongoDBConfig {

    private static final MongodStarter STARTER = MongodStarter.getDefaultInstance();

    public static MongodExecutable mongodExecutable() throws Exception {
        IMongodConfig mongoConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(AppProps.instance().getDbMongoHost(), AppProps.instance().getDbMongoPort(), Network.localhostIsIPv6()))
                .timeout(new Timeout(60000))
                .build();

        return STARTER.prepare(mongoConfig);
    }

    public static MongoDatabase synchronizedMongoDB() {
        MongoClient mongoClient = new MongoClient(AppProps.instance().getDbMongoHost(), AppProps.instance().getDbMongoPort());
        return mongoClient.getDatabase(AppProps.instance().getDbMongoDatabase());
    }
}
