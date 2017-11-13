package pl.ark.chr.timelyzer.auth;

import com.mongodb.DBObject;
import org.pac4j.core.exception.CredentialsException;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import pl.ark.chr.timelyzer.persistence.User;
import pl.ark.chr.timelyzer.util.MongoObjectConverter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
        throwable.printStackTrace();
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

    public void await() throws InterruptedException {
        if(!latch.await(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
            throw new CredentialsException("Error waiting for data from db. User will not be authenticated");
        }
    }
}