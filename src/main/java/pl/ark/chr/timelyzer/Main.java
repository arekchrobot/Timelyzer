package pl.ark.chr.timelyzer;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import pl.ark.chr.timelyzer.config.MongoConfig;
import pl.ark.chr.timelyzer.config.Server;
import pl.ark.chr.timelyzer.persistence.Project;
import pl.ark.chr.timelyzer.persistence.TimeTrack;
import pl.ark.chr.timelyzer.rest.TestEndpoint;
import pl.ark.chr.timelyzer.util.MongoObjectConverter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Arek on 2017-06-17.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Server server = new Server(new TestEndpoint());
        server.start();

        TimeTrack timeTrack = TimeTrack.builder().comment("hehehe").dayOfIssue(LocalDate.now()).duration(120).user("test@test.test").build();

        Set<TimeTrack> tracks = new HashSet<>();
        tracks.add(timeTrack);
        Project project = Project.builder().name("TestProj").timeTracks(tracks).build();

        DBObject dbObject = MongoObjectConverter.toDBObject(project);
        System.out.println(JSON.serialize(dbObject));
        MongoConfig.close();
        server.stop();
    }
}
