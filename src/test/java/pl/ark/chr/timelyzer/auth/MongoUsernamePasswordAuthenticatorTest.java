package pl.ark.chr.timelyzer.auth;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import org.junit.*;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import pl.ark.chr.timelyzer.repository.UserRepository;
import pl.wkr.fluentrule.api.FluentExpectedException;
import util.EmbedMongoDBConfig;

import static org.assertj.core.api.Assertions.*;

public class MongoUsernamePasswordAuthenticatorTest {

    @Rule
    public FluentExpectedException fluentThrown = FluentExpectedException.none();

    private static MongodExecutable mongodExecutable;
    private static MongodProcess mongod;
    private MongoUsernamePasswordAuthenticator sut = new MongoUsernamePasswordAuthenticator(new UserRepository());

    @BeforeClass
    public static void setUp() throws Exception {
        String testUser = "{\"_id\":\"test\",\"password\":\"$2a$11$n8IPQk8LfidgekWd/l9cHOc/UPA9GHjoMISeyWYDMIlIdfBH8PYBK\"}";
        mongodExecutable = EmbedMongoDBConfig.mongodExecutable();
        mongod = mongodExecutable.start();
        EmbedMongoDBConfig.synchronizedMongoDB().getCollection("users", DBObject.class)
                .insertOne(BasicDBObject.parse(testUser));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        mongod.stop();
        mongodExecutable.stop();
    }

    @Test
    public void shouldInsertProfileIntoUser() throws Exception {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("test", "test", "client");

        sut.validate(credentials);

        assertThat(credentials.getUserProfile()).isNotNull();
        assertThat(credentials.getUserProfile().getId()).isEqualTo("test");
        assertThat(credentials.getUserProfile().getAttribute("username")).isNotNull().isEqualTo("test");
    }

    @Test
    public void shouldThrowErrorWhenEmptyUsername() throws Exception {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(null, "test", "client");

        fluentThrown
                .expect(CredentialsException.class)
                .hasMessage("Username cannot be blank");

        sut.validate(credentials);
    }

    @Test
    public void shouldThrowErrorWhenEmptyPassword() throws Exception {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("test", "", "client");

        fluentThrown
                .expect(CredentialsException.class)
                .hasMessage("Password cannot be blank");

        sut.validate(credentials);
    }

    @Test
    public void shouldThrowErrorWhenUserNotFound() throws Exception {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("test123", "test", "client");

        fluentThrown
                .expect(CredentialsException.class)
                .hasMessage("Username or password is wrong");

        sut.validate(credentials);
    }

    @Test
    public void shouldThrowErrorWhenPasswordsDoNotMatch() throws Exception {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("test", "test123", "client");

        fluentThrown
                .expect(CredentialsException.class)
                .hasMessage("Passwords not match");

        sut.validate(credentials);
    }
}