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
import pl.ark.chr.timelyzer.persistence.TrackType;
import pl.ark.chr.timelyzer.repository.ProjectRepository;
import util.EmbedMongoDBConfig;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static util.DBTestUtil.*;

public class ProjectServiceTest {

    private static MongodExecutable mongodExecutable;
    private static MongodProcess mongod;

    private ProjectService sut = new ProjectService(new ProjectRepository());

    @BeforeClass
    public static void setUp() throws Exception {
        mongodExecutable = EmbedMongoDBConfig.mongodExecutable();
        mongod = mongodExecutable.start();
        EmbedMongoDBConfig.synchronizedMongoDB().getCollection("users", DBObject.class)
                .insertMany(Arrays.asList(BasicDBObject.parse(testUser), BasicDBObject.parse(testUser2)));
        EmbedMongoDBConfig.synchronizedMongoDB().getCollection("projects", DBObject.class)
                .insertMany(Arrays.asList(BasicDBObject.parse(testProject), BasicDBObject.parse(testProject2), BasicDBObject.parse(testProject3)));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        mongod.stop();
        mongodExecutable.stop();
    }

    @Test
    public void shouldReturnProjectsForUsersWithEmptyUsersAndTimeTracks() {
        final CountDownLatch latch = new CountDownLatch(1);
        final List<Project> retrievedProjects = new ArrayList<>();
        sut.getAllProjectsForUser("test").thenAccept(projects -> {
            try {
                retrievedProjects.addAll(projects);
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await(2000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail("Waited too long for async task to finish");
        }

        assertThat(retrievedProjects).isNotNull().isNotEmpty().hasSize(1);
        assertThat(retrievedProjects.get(0).getName()).isEqualTo("TestProj");
        assertThat(retrievedProjects.get(0).getUsers()).isNotNull().isEmpty();
        assertThat(retrievedProjects.get(0).getTimeTracks()).isNotNull().isEmpty();
        assertThat(retrievedProjects.get(0).getId()).isNotNull();
    }

    @Test
    public void shouldReturnEmptyArrayWhenWrongUserPassed() {
        final CountDownLatch latch = new CountDownLatch(1);
        final List<Project> retrievedProjects = new ArrayList<>();
        sut.getAllProjectsForUser("wrongTest").thenAccept(projects -> {
            try {
                retrievedProjects.addAll(projects);
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await(2000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail("Waited too long for async task to finish");
        }
        assertThat(retrievedProjects).isNotNull().isEmpty();
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

    @Test
    public void shouldReturnSummedUpValuesForLastWeekForUserByTrackType() {
        final CountDownLatch latch = new CountDownLatch(1);
        final Map<TrackType, Integer> retrievedProjects = new HashMap<>();
        sut.getWeeklySumUpForTrackTypes("test@gmail.com").thenAccept(trackTypeIntegerMap -> {
            try {
                retrievedProjects.putAll(trackTypeIntegerMap);
            } catch (Exception ex) {

                ex.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await(2000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail("Waited too long for async task to finish");
        }
        assertThat(retrievedProjects).isNotNull().isNotEmpty().hasSize(2);
        assertThat(retrievedProjects.keySet()).containsOnly(TrackType.DEVELOPMENT, TrackType.RESEARCH);
        assertThat(retrievedProjects.get(TrackType.DEVELOPMENT)).isEqualTo(5);
        assertThat(retrievedProjects.get(TrackType.RESEARCH)).isEqualTo(3);
    }

    @Test
    public void shouldConvertToWeeklySumUpForTrackTypes() {
        TimeTrack t1 = TimeTrack.builder().type(TrackType.BUGFIX).duration(2).build();
        TimeTrack t2 = TimeTrack.builder().type(TrackType.DEVELOPMENT).duration(2).build();
        TimeTrack t3 = TimeTrack.builder().type(TrackType.DEVELOPMENT).duration(5).build();
        TimeTrack t4 = TimeTrack.builder().type(TrackType.RESEARCH).duration(2).build();
        TimeTrack t5 = TimeTrack.builder().type(TrackType.DEVELOPMENT).duration(1).build();
        TimeTrack t6 = TimeTrack.builder().type(TrackType.BUGFIX).duration(8).build();

        Map<TrackType, Integer> result = sut.convertToWeeklySumUpForTrackTypes(Arrays.asList(t1, t2, t3, t4, t5, t6));

        assertThat(result.keySet()).isNotNull().isNotEmpty().hasSize(3);
        assertThat(result.get(TrackType.DEVELOPMENT)).isNotNull().isEqualTo(8);
        assertThat(result.get(TrackType.RESEARCH)).isNotNull().isEqualTo(2);
        assertThat(result.get(TrackType.BUGFIX)).isNotNull().isEqualTo(10);
    }

    @Test
    public void shouldReturnWeeklySumUpForProjects() {
        final CountDownLatch latch = new CountDownLatch(1);
        final Map<String, Map<String, Integer>> weeklySummary = new LinkedHashMap<>();
        sut.getWeeklySumUpForProjects("test@gmail.com").thenAccept(weeklySummaryForProjects -> {
            try {
                weeklySummary.putAll(weeklySummaryForProjects);
            } catch (Exception ex) {

                ex.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await(2000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail("Waited too long for async task to finish");
        }

        List<String> lastWeekDates = generateLastWeekDates();

        assertThat(weeklySummary).isNotNull().isNotEmpty().hasSize(8);
        assertThat(weeklySummary.keySet()).containsExactlyElementsOf(lastWeekDates);
        weeklySummary.values().forEach(projectTimeMap -> assertThat(projectTimeMap).isNotEmpty().hasSize(2));
        assertThat(weeklySummary.get(lastWeekDates.get(4)).get("Project1Test")).isEqualTo(3);
        assertThat(weeklySummary.get(lastWeekDates.get(6)).get("Project2Test")).isEqualTo(5);
        assertThat(weeklySummary.get(lastWeekDates.get(7)).get("Project1Test")).isEqualTo(0);
        assertThat(weeklySummary.get(lastWeekDates.get(7)).get("Project2Test")).isEqualTo(0);
    }

    @Test
    public void shouldConvertToSumUpObject() {
        Project p1 = createTestProject("TestProj");
        Project p2 = createTestProject("TestProj2");
        Project p3 = createTestProject("TestProj3");

        Map<String, Map<LocalDate, Integer>> projectSumUps = sut.convertToSumUpObject(Arrays.asList(p1, p2, p3));

        assertThat(projectSumUps).isNotNull().isNotEmpty().hasSize(3);
        projectSumUps.keySet().forEach(project -> assertThat(projectSumUps.get(project)).isNotNull().isNotEmpty().hasSize(4));
    }

    @Test
    public void shouldCreateWeeklySumUpFromProject() {
        Project p = createTestProject("TestProj");

        ProjectService.ProjectWeeklySumUp projectWeeklySumUp = sut.convertToSumUpObject(p);

        assertThat(projectWeeklySumUp).isNotNull();
        assertThat(projectWeeklySumUp.getName()).isEqualTo("TestProj");
        assertThat(projectWeeklySumUp.getSummedTimeTracks()).isNotNull().isNotEmpty();
        assertThat(projectWeeklySumUp.getSummedTimeTracks().keySet()).hasSize(4);
        assertThat(projectWeeklySumUp.getSummedTimeTracks().get(LocalDate.now().minusDays(8))).isEqualTo(8);
        assertThat(projectWeeklySumUp.getSummedTimeTracks().get(LocalDate.now().minusDays(3))).isEqualTo(3);
        assertThat(projectWeeklySumUp.getSummedTimeTracks().get(LocalDate.now().minusDays(2))).isEqualTo(2);
        assertThat(projectWeeklySumUp.getSummedTimeTracks().get(LocalDate.now().minusDays(1))).isEqualTo(7);
    }

    @Test
    public void shouldConvertToWeeklySumUpForProjects() {
        Project p1 = createTestProject("TestProj");
        Project p2 = createTestProject("TestProj2");
        Project p3 = createTestProject("TestProj3");

        Map<String, Map<LocalDate, Integer>> projectSumUps = sut.convertToSumUpObject(Arrays.asList(p1, p2, p3));
        Map<String, Map<String, Integer>> weeklySumUpForProjects = sut.convertToWeeklySumUpForProjects(projectSumUps);

        List<String> lastWeekDates = generateLastWeekDates();

        assertThat(weeklySumUpForProjects).isNotNull().isNotEmpty().hasSize(8);
        assertThat(weeklySumUpForProjects.keySet()).containsExactlyElementsOf(lastWeekDates);
        weeklySumUpForProjects.keySet().forEach(date -> assertThat(weeklySumUpForProjects.get(date)).hasSize(3));
        assertThat(weeklySumUpForProjects.get(lastWeekDates.get(2)).get("TestProj")).isEqualTo(0);
        assertThat(weeklySumUpForProjects.get(lastWeekDates.get(5)).get("TestProj2")).isEqualTo(2);
        assertThat(weeklySumUpForProjects.get(lastWeekDates.get(6)).get("TestProj3")).isEqualTo(7);
        assertThat(weeklySumUpForProjects.get(lastWeekDates.get(7)).get("TestProj3")).isEqualTo(0);
    }

    private Project createTestProject(String projectName) {
        TimeTrack t1 = TimeTrack.builder().dayOfIssue(LocalDate.now().minusDays(3)).duration(2).build();
        TimeTrack t2 = TimeTrack.builder().dayOfIssue(LocalDate.now().minusDays(2)).duration(2).build();
        TimeTrack t3 = TimeTrack.builder().dayOfIssue(LocalDate.now().minusDays(1)).duration(5).build();
        TimeTrack t4 = TimeTrack.builder().dayOfIssue(LocalDate.now().minusDays(1)).duration(2).build();
        TimeTrack t5 = TimeTrack.builder().dayOfIssue(LocalDate.now().minusDays(3)).duration(1).build();
        TimeTrack t6 = TimeTrack.builder().dayOfIssue(LocalDate.now().minusDays(8)).duration(8).build();

        return Project.builder().name(projectName).timeTracks(Stream.of(t1, t2, t3, t4, t5, t6).collect(Collectors.toSet())).build();
    }

    private List<String> generateLastWeekDates() {
        LocalDate d1 = LocalDate.now().minusDays(ProjectService.LAST_WEEK);
        LocalDate d2 = LocalDate.now().minusDays(ProjectService.LAST_WEEK-1);
        LocalDate d3 = LocalDate.now().minusDays(ProjectService.LAST_WEEK-2);
        LocalDate d4 = LocalDate.now().minusDays(ProjectService.LAST_WEEK-3);
        LocalDate d5 = LocalDate.now().minusDays(ProjectService.LAST_WEEK-4);
        LocalDate d6 = LocalDate.now().minusDays(ProjectService.LAST_WEEK-5);
        LocalDate d7 = LocalDate.now().minusDays(ProjectService.LAST_WEEK-6);
        LocalDate d8 = LocalDate.now();

        return Stream.of(d1,d2,d3,d4,d5,d6,d7,d8)
                .map(localDate -> localDate.format(ProjectService.FORMATTER))
                .collect(Collectors.toList());
    }
}