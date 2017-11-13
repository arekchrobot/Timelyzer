package pl.ark.chr.timelyzer.repository;

import com.mongodb.DBObject;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;
import pl.ark.chr.timelyzer.config.MongoConfig;
import pl.ark.chr.timelyzer.persistence.User;

import static com.mongodb.client.model.Filters.*;

public class UserRepository extends CrudRepositoryImpl<User> implements CrudRepository<User> {

    @Override
    protected MongoCollection<DBObject> getCollection() {
        return MongoConfig.mongoDatabase().getCollection("users", DBObject.class);
    }

    public Publisher<DBObject> findByUsernameAndPassword(String username, String password) {
        Bson filter = and(eq("_id", username), eq("password", password));

        return getCollection().find(filter).first();
    }
}
