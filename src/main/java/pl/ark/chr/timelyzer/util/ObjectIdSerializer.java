package pl.ark.chr.timelyzer.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdSerializer extends JsonSerializer<ObjectId> {

    class IdHolder {
        private String $oid;

        public IdHolder(String $oid) {
            this.$oid = $oid;
        }

        public String get$oid() {
            return $oid;
        }
    }

    @Override
    public void serialize(ObjectId objectId, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeObject(new IdHolder(objectId.toString()));
    }
}
