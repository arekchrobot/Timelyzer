package pl.ark.chr.timelyzer.repository;

import com.mongodb.DBObject;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;
import pl.ark.chr.timelyzer.config.MongoConfig;
import pl.ark.chr.timelyzer.persistence.Project;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;

import static com.mongodb.client.model.Accumulators.first;
import static com.mongodb.client.model.Accumulators.push;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.in;

public class ProjectRepository extends CrudRepositoryImpl<Project> implements CrudRepository<Project> {

    @Override
    protected MongoCollection<DBObject> getCollection() {
        return MongoConfig.instance().mongoDatabase().getCollection("projects", DBObject.class);
    }

    public Publisher<DBObject> findAllByUser(String username) {
        Bson filter = in("users", username);
        return getCollection().find(filter);
    }

    public Publisher<DBObject> findAllByUserAndTracksTimeAgg(String username, LocalDate date) {
        return getCollection()
                .aggregate(Arrays.asList(
                        match(in("users", username)),
                        unwind("$timeTracks"),
                        match(gte("timeTracks.dayOfIssue", date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())),
                        group("$_id", first("name", "$name"), push("timeTracks", "$timeTracks"))
                        ),
                        DBObject.class);
    }
}
