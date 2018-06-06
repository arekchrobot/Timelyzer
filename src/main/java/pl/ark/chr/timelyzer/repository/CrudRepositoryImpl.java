package pl.ark.chr.timelyzer.repository;

import com.mongodb.DBObject;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.Success;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;
import pl.ark.chr.timelyzer.util.MongoObjectConverter;

import java.lang.reflect.ParameterizedType;

import static com.mongodb.client.model.Filters.*;

public abstract class CrudRepositoryImpl<T extends Object> implements CrudRepository<T> {

    protected abstract MongoCollection<DBObject> getCollection();

    protected Class<T> entityClass;

    public CrudRepositoryImpl() {
        ParameterizedType thisType = (ParameterizedType) getClass().getGenericSuperclass();
        entityClass = (Class<T>) thisType.getActualTypeArguments()[0];
    }


    @Override
    public Publisher<Success> save(T entity) {
        DBObject dbObject = MongoObjectConverter.toDBObject(entity);
        return getCollection().insertOne(dbObject);
    }

    @Override
    public Publisher<DBObject> get(Object id) {
        Bson filter = eq("_id", id);
        return getCollection().find(filter).first();
    }

    @Override
    public Publisher<DBObject> getAll() {
        return getCollection().find();
    }

    @Override
    public Publisher<DeleteResult> deleteAll() {
        return getCollection().deleteMany(new Document());
    }
}
