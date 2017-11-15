package pl.ark.chr.timelyzer.persistence;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import pl.ark.chr.timelyzer.util.ObjectIdDeserializer;
import pl.ark.chr.timelyzer.util.ObjectIdSerializer;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(value = "projects", noClassnameStored = true)
public class Project {

    @Id
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id;
    private Set<String> users;
    private String name;
    @Embedded
    private Set<TimeTrack> timeTracks;
}
