package pl.ark.chr.timelyzer.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Arek on 2017-06-18.
 */
public class ApplicationProperties {

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

    private static final Properties config = new Properties();

    static {
        try(InputStream is = ApplicationProperties.class.getClassLoader().getResourceAsStream("application.properties")) {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getServerPort() {
        return Integer.parseInt(config.getProperty(SERVER_PORT));
    }

    public static int getServerThreads() {
        return Integer.parseInt(config.getProperty(SERVER_THREADS));
    }

    public static String getServerAddress() {
        return config.getProperty(SERVER_ADDRESS);
    }

    public static String getJwtSalt() {
        return config.getProperty(JWT_SALT);
    }

    public static String getAuthHeader() {
        return config.getProperty(AUTH_HEADER);
    }

    public static String getAuthPrefixHeader() {
        return config.getProperty(AUTH_PREFIX_HEADER);
    }

    public static String getDbMongoHost() {
        return config.getProperty(DB_MONGO_HOST);
    }

    public static String getDbMongoDatabase() {
        return config.getProperty(DB_MONGO_DATABASE);
    }

    public static int getDbMongoPort() {
        return Integer.parseInt(config.getProperty(DB_MONGO_PORT));
    }

    public static int getBCryptStrength() {
        return Integer.parseInt(config.getProperty(BCRYPT_STRENGTH));
    }
}
