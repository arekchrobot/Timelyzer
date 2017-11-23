package pl.ark.chr.timelyzer.util.subscribers;

import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class ListDBObjectSubscriber implements Subscriber<DBObject> {

    private List<DBObject> retrievedEntities = new ArrayList<>();
    private final Consumer<List<DBObject>> consumer;
    private final long numberOfRequests;

    public ListDBObjectSubscriber(Consumer<List<DBObject>> consumer) {
        this(consumer, Long.MAX_VALUE);
    }

    public ListDBObjectSubscriber(Consumer<List<DBObject>> consumer, long numberOfRequests) {
        this.consumer = consumer;
        this.numberOfRequests = numberOfRequests;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(numberOfRequests);
    }

    @Override
    public void onNext(DBObject dbObject) {
        retrievedEntities.add(dbObject);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("Error getting list of dbObjects from mongodb", throwable);
        onComplete();
    }

    @Override
    public void onComplete() {
        consumer.accept(retrievedEntities);
    }
}
