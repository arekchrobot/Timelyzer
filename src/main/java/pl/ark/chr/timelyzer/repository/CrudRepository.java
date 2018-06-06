package pl.ark.chr.timelyzer.repository;

import com.mongodb.DBObject;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.reactivestreams.client.Success;
import org.reactivestreams.Publisher;

public interface CrudRepository<T extends Object> {

    Publisher<Success> save(T entity);

    Publisher<DBObject> get(Object id);

    Publisher<DBObject> getAll();

    Publisher<DeleteResult> deleteAll();
}
