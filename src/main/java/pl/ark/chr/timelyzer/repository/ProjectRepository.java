package pl.ark.chr.timelyzer.repository;

import com.mongodb.DBObject;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;
import pl.ark.chr.timelyzer.config.MongoConfig;
import pl.ark.chr.timelyzer.persistence.Project;

import static com.mongodb.client.model.Filters.in;

public class ProjectRepository extends CrudRepositoryImpl<Project> implements CrudRepository<Project> {

    @Override
    protected MongoCollection<DBObject> getCollection() {
        return MongoConfig.mongoDatabase().getCollection("projects", DBObject.class);
    }

    public Publisher<DBObject> findAllByUser(String username) {
        Bson filter = in("users", username);
        return getCollection().find(filter);
    }
}
