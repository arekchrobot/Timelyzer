package pl.ark.chr.timelyzer.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(value = "projects", noClassnameStored = true)
public class Project {

    @Id
    private ObjectId id;
    private Set<String> users;
    private String name;
    @Embedded
    private Set<TimeTrack> timeTracks;
}
