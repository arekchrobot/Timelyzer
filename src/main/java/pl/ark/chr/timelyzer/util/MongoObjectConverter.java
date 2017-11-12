package pl.ark.chr.timelyzer.util;

import com.mongodb.DBObject;
import org.mongodb.morphia.mapping.Mapper;
import pl.ark.chr.timelyzer.config.MongoConfig;

public class MongoObjectConverter {

    public static <T extends Object> T toPojo(DBObject entity, Class<T> targetClass) {
        //datastore as null since we only use for mapping
        Mapper mapper = MongoConfig.morphia().getMapper();
        return mapper.fromDBObject(null, targetClass, entity, mapper.createEntityCache());
    }

    public static DBObject toDBObject(Object entity) {
        return MongoConfig.morphia().getMapper().toDBObject(entity);
    }
}
