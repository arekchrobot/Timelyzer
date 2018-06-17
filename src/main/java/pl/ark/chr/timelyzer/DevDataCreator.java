package pl.ark.chr.timelyzer;

import com.mongodb.BasicDBObject;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import pl.ark.chr.timelyzer.factory.DaoFactory;
import pl.ark.chr.timelyzer.persistence.Project;
import pl.ark.chr.timelyzer.persistence.User;
import pl.ark.chr.timelyzer.repository.ProjectRepository;
import pl.ark.chr.timelyzer.repository.UserRepository;
import pl.ark.chr.timelyzer.util.MongoObjectConverter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DevDataCreator {

    private static final ZoneId zone = ZoneId.systemDefault();

    public static final String testUser = "{\"_id\":\"test\",\"password\":\"$2a$11$n8IPQk8LfidgekWd/l9cHOc/UPA9GHjoMISeyWYDMIlIdfBH8PYBK\"}";
    public static final String testUser2 = "{\"_id\":\"test@gmail.com\",\"password\":\"$2a$11$n8IPQk8LfidgekWd/l9cHOc/UPA9GHjoMISeyWYDMIlIdfBH8PYBK\"}";

    public static final String testProject = "{\"_id\":{\"$oid\":\"5a171af5fe8f0ac220956566\"},\"users\":[\"test\"],\"name\":\"TestProj\",\"timeTracks\":null}";
    public static final String testProject2 = "{\"_id\":{\"$oid\":\"5a526ff4b9ec391993c94e1b\"}," +
            "\"name\":\"Project2Test\"," +
            "\"users\":[\"test@gmail.com\", \"test2@gmail.com\"]," +
            "\"timeTracks\": [" +
            "{\"dayOfIssue\": " + LocalDate.now().minusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() + ",\"user\":\"test@gmail.com\",\"comment\":\"Research\",duration: 3,type: \"RESEARCH\"}," +
            "{\"dayOfIssue\": " + LocalDate.now().minusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() + ",\"user\":\"test@gmail.com\",\"comment\":\"Implement\",duration: 2,type: \"DEVELOPMENT\"}," +
            "{\"dayOfIssue\": " + LocalDate.now().minusDays(8).atStartOfDay(zone).toInstant().toEpochMilli() + ",\"user\":\"test2@gmail.com\",\"comment\":\"Research\",duration: 8,type: \"DEVELOPMENT\"}" +
            "]}";
    public static final String testProject3 = "{\"_id\":{\"$oid\":\"5a5262deb9ec391993c94e1a\"}," +
            "\"name\":\"Project1Test\"," +
            "\"users\":[\"test@gmail.com\"]," +
            "\"timeTracks\": [" +
            "{\"dayOfIssue\": " + LocalDate.now().minusDays(3).atStartOfDay(zone).toInstant().toEpochMilli() + ",\"user\":\"test@gmail.com\",\"comment\":\"Research\",duration: 3,type: \"DEVELOPMENT\"}" +
            "]}";

    private final static UserRepository userRepository = DaoFactory.userRepository();
    private final static ProjectRepository projectRepository = DaoFactory.projectRepository();

    public static void createData() {
        createUsers();
        createProjects();
    }

    private static void createUsers() {
        SyncSubscriber syncSub = new SyncSubscriber();
        userRepository.deleteAll().subscribe(syncSub);
        syncSub.await();
        userRepository.save(MongoObjectConverter.toPojo(BasicDBObject.parse(testUser), User.class)).subscribe(new VoidSubscriber<>());
        userRepository.save(MongoObjectConverter.toPojo(BasicDBObject.parse(testUser2), User.class)).subscribe(new VoidSubscriber<>());
    }

    private static void createProjects() {
        SyncSubscriber syncSub = new SyncSubscriber();
        projectRepository.deleteAll().subscribe(syncSub);
        syncSub.await();
        projectRepository.save(MongoObjectConverter.toPojo(BasicDBObject.parse(testProject), Project.class)).subscribe(new VoidSubscriber<>());
        projectRepository.save(MongoObjectConverter.toPojo(BasicDBObject.parse(testProject2), Project.class)).subscribe(new VoidSubscriber<>());
        projectRepository.save(MongoObjectConverter.toPojo(BasicDBObject.parse(testProject3), Project.class)).subscribe(new VoidSubscriber<>());
    }

    private static class VoidSubscriber<T> implements Subscriber<T> {
        @Override
        public void onSubscribe(final Subscription s) {
            s.request(1L);
        }

        @Override
        public void onNext(final T t) {

        }

        @Override
        public void onError(final Throwable t) {

        }

        @Override
        public void onComplete() {

        }
    }

    private static class SyncSubscriber<T> implements Subscriber<T> {
        private final CountDownLatch latch;

        SyncSubscriber() {
            this.latch = new CountDownLatch(1);
        }
        @Override
        public void onSubscribe(final Subscription s) {
            s.request(1L);
        }

        @Override
        public void onNext(final T t) {

        }

        @Override
        public void onError(final Throwable t) {
            latch.countDown();
        }

        @Override
        public void onComplete() {
            latch.countDown();
        }

        void await() {
            try {
                if(!latch.await(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                }
            } catch (InterruptedException e) {
            }
        }
    }
}
