package pl.ark.chr.timelyzer.project;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pl.ark.chr.timelyzer.persistence.Project;
import pl.ark.chr.timelyzer.persistence.TimeTrack;
import pl.ark.chr.timelyzer.repository.ProjectRepository;
import util.EmbedMongoDBConfig;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectServiceTest {

    private static MongodExecutable mongodExecutable;
    private static MongodProcess mongod;

    private ProjectService sut = new ProjectService(new ProjectRepository());

    @BeforeClass
    public static void setUp() throws Exception {
        String testUser = "{\"_id\":\"test\",\"password\":\"$2a$11$n8IPQk8LfidgekWd/l9cHOc/UPA9GHjoMISeyWYDMIlIdfBH8PYBK\"}";
        String testProject = "{\"id\":{\"$oid\":\"5a171af5fe8f0ac220956566\"},\"users\":null,\"name\":\"TestProj\",\"timeTracks\":null}";
        mongodExecutable = EmbedMongoDBConfig.mongodExecutable();
        mongod = mongodExecutable.start();
        EmbedMongoDBConfig.synchronizedMongoDB().getCollection("users", DBObject.class)
                .insertOne(BasicDBObject.parse(testUser));
        EmbedMongoDBConfig.synchronizedMongoDB().getCollection("projects", DBObject.class)
                .insertOne(BasicDBObject.parse(testProject));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        mongod.stop();
        mongodExecutable.stop();
    }

    @Test
    public void shouldReturnProjectsForUsersWithEmptyUsersAndTimeTracks() {
        sut.getAllProjectsForUser("test").thenAccept(projects -> {
            assertThat(projects).isNotNull().isNotEmpty().hasSize(1);
            assertThat(projects.get(0).getName()).isEqualTo("TestProj");
            assertThat(projects.get(0).getUsers()).isNotNull().isEmpty();
            assertThat(projects.get(0).getTimeTracks()).isNotNull().isEmpty();
            assertThat(projects.get(0).getId()).isNotNull();
        });
    }

    @Test
    public void shouldReturnEmptyArrayWhenWrongUserPassed() {
        sut.getAllProjectsForUser("wrongTest").thenAccept(projects -> {
            assertThat(projects).isNotNull().isEmpty();
        });
    }

    @Test
    public void shouldClearUsersAndTimeTracksForProject() {
        Project project = Project.builder()
                .name("TestProj")
                .id(new ObjectId())
                .users(new HashSet<>(Arrays.asList("test")))
                .timeTracks(new HashSet<>(Arrays.asList(TimeTrack.builder().comment("test").user("test").build())))
                .build();

        List<Project> projects = sut.emptyUsersAndTracks(Arrays.asList(project));

        assertThat(projects).isNotNull().isNotEmpty().hasSize(1);
        assertThat(projects.get(0).getUsers()).isNotNull().isEmpty();
        assertThat(projects.get(0).getTimeTracks()).isNotNull().isEmpty();
        assertThat(projects.get(0).getName()).isNotNull().isEqualTo("TestProj");
    }
}