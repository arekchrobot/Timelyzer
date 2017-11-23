package pl.ark.chr.timelyzer.util.subscribers;

import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class SingleDBObjectSubscriber implements Subscriber<DBObject> {

    private Optional<DBObject> retrievedEntity = Optional.empty();
    private final Consumer<Optional<DBObject>> consumer;

    public SingleDBObjectSubscriber(Consumer<Optional<DBObject>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(1L);
    }

    @Override
    public void onNext(DBObject dbObject) {
        retrievedEntity = Optional.of(dbObject);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("Error in getting object from mongodb", throwable);
        onComplete();
    }

    @Override
    public void onComplete() {
        consumer.accept(retrievedEntity);
    }
}
