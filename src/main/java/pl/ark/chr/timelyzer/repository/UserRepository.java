package pl.ark.chr.timelyzer.repository;

import com.mongodb.DBObject;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;
import pl.ark.chr.timelyzer.config.MongoConfig;
import pl.ark.chr.timelyzer.persistence.User;

import static com.mongodb.client.model.Filters.eq;

public class UserRepository extends CrudRepositoryImpl<User> implements CrudRepository<User> {

    @Override
    protected MongoCollection<DBObject> getCollection() {
        return MongoConfig.instance().mongoDatabase().getCollection("users", DBObject.class);
    }

    public Publisher<DBObject> findByUsername(String username) {
        Bson filter = eq("_id", username);

        return getCollection().find(filter).first();
    }
}
