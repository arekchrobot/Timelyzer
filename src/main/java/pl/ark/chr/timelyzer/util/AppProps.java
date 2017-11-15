package pl.ark.chr.timelyzer.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Arek on 2017-06-18.
 */
public class AppProps {

    private static final String SERVER_PORT = "server.port";
    private static final String SERVER_ADDRESS = "server.address";
    private static final String SERVER_THREADS = "server.threads";
    private static final String JWT_SALT = "jwt.salt";
    private static final String AUTH_HEADER = "headerClient.headerName";
    private static final String AUTH_PREFIX_HEADER = "headerClient.PrefixHeader";
    private static final String DB_MONGO_HOST = "db.mongo.host";
    private static final String DB_MONGO_DATABASE = "db.mongo.database";
    private static final String DB_MONGO_PORT = "db.mongo.port";
    private static final String BCRYPT_STRENGTH = "bCrypt.strength";

    private final Properties config = new Properties();

    private static AppProps instance;

    private AppProps() {
        try (InputStream is = AppProps.class.getClassLoader().getResourceAsStream("application.properties")) {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AppProps instance() {
        if(instance == null) {
            synchronized (AppProps.class) {
                if(instance == null) {
                    instance = new AppProps();
                }
            }
        }
        return instance;
    }

    public int getServerPort() {
        return Integer.parseInt(config.getProperty(SERVER_PORT));
    }

    public int getServerThreads() {
        return Integer.parseInt(config.getProperty(SERVER_THREADS));
    }

    public String getServerAddress() {
        return config.getProperty(SERVER_ADDRESS);
    }

    public String getJwtSalt() {
        return config.getProperty(JWT_SALT);
    }

    public String getAuthHeader() {
        return config.getProperty(AUTH_HEADER);
    }

    public String getAuthPrefixHeader() {
        return config.getProperty(AUTH_PREFIX_HEADER);
    }

    public String getDbMongoHost() {
        return config.getProperty(DB_MONGO_HOST);
    }

    public String getDbMongoDatabase() {
        return config.getProperty(DB_MONGO_DATABASE);
    }

    public int getDbMongoPort() {
        return Integer.parseInt(config.getProperty(DB_MONGO_PORT));
    }

    public int getBCryptStrength() {
        return Integer.parseInt(config.getProperty(BCRYPT_STRENGTH));
    }
}
