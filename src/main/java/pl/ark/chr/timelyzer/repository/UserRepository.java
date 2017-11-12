package pl.ark.chr.timelyzer.repository;

import com.mongodb.DBObject;
import com.mongodb.reactivestreams.client.MongoCollection;
import pl.ark.chr.timelyzer.config.MongoConfig;
import pl.ark.chr.timelyzer.persistence.User;

public class UserRepository extends CrudRepositoryImpl<User> implements CrudRepository<User> {

    @Override
    protected MongoCollection<DBObject> getCollection() {
        return MongoConfig.mongoDatabase().getCollection("users", DBObject.class);
    }
}
