package pl.ark.chr.timelyzer.auth;

import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.exception.CredentialsException;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import pl.ark.chr.timelyzer.persistence.User;
import pl.ark.chr.timelyzer.util.MongoObjectConverter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
class AuthSubscriber implements Subscriber<DBObject> {

    private final Consumer<User> consumer;
    private DBObject retrievedUser;
    private final CountDownLatch latch;

    AuthSubscriber(Consumer<User> consumer) {
        this.consumer = consumer;
        this.latch = new CountDownLatch(1);
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(1L);
    }

    @Override
    public void onNext(DBObject dbObject) {
        this.retrievedUser = dbObject;
    }

    @Override
    public void onError(Throwable throwable) {
        latch.countDown();
        log.error("Error getting user from database to auth.", throwable);
    }

    @Override
    public void onComplete() {
        User user = null;
        if (this.retrievedUser != null) {
            user = MongoObjectConverter.toPojo(this.retrievedUser, User.class);
        }
        this.consumer.accept(user);
        latch.countDown();
    }

    void await() {
        try {
            if(!latch.await(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                throw new CredentialsException("Error waiting for data from db. User will not be authenticated");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Error while authenticating user", e);
        }
    }
}