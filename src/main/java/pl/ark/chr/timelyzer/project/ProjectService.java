package pl.ark.chr.timelyzer.project;

import com.mongodb.DBObject;
import org.reactivestreams.Subscriber;
import pl.ark.chr.timelyzer.persistence.Project;
import pl.ark.chr.timelyzer.repository.ProjectRepository;
import pl.ark.chr.timelyzer.util.MongoObjectConverter;
import pl.ark.chr.timelyzer.util.subscribers.ListDBObjectSubscriber;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ProjectService {

    private ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    CompletableFuture<List<Project>> getAllProjectsForUser(String username) {

        CompletableFuture<List<Project>> retrievedProjects = new CompletableFuture<>();

        Subscriber<DBObject> subscriber = new ListDBObjectSubscriber(dbObjects ->
                retrievedProjects.complete(dbObjects.stream()
                        .map(dbObject -> MongoObjectConverter.toPojo(dbObject, Project.class))
                        .collect(Collectors.toList()))
        );

        projectRepository.findAllByUser(username).subscribe(subscriber);

        return retrievedProjects.thenApplyAsync(this::emptyUsersAndTracks);
    }

    List<Project> emptyUsersAndTracks(List<Project> projects) {
        projects.forEach(project -> {
            project.setTimeTracks(new HashSet<>());
            project.setUsers(new HashSet<>());
        });
        return projects;
    }
}
