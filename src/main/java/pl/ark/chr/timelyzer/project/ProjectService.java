package pl.ark.chr.timelyzer.project;

import com.mongodb.DBObject;
import org.reactivestreams.Subscriber;
import pl.ark.chr.timelyzer.persistence.Project;
import pl.ark.chr.timelyzer.persistence.TimeTrack;
import pl.ark.chr.timelyzer.persistence.TrackType;
import pl.ark.chr.timelyzer.repository.ProjectRepository;
import pl.ark.chr.timelyzer.util.MongoObjectConverter;
import pl.ark.chr.timelyzer.util.subscribers.ListDBObjectSubscriber;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectService {

    private ProjectRepository projectRepository;

    final static int LAST_WEEK = 7;

    final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public CompletableFuture<List<Project>> getAllProjectsForUser(String username) {

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

    public CompletableFuture<Map<TrackType, Integer>> getWeeklySumUpForTrackTypes(String username) {
        CompletableFuture<List<TimeTrack>> retrievedTimeTracks = new CompletableFuture<>();

        LocalDate lastWeek = LocalDate.now().minusDays(LAST_WEEK);

        Subscriber<DBObject> subscriber = new ListDBObjectSubscriber(dbObjects ->
                retrievedTimeTracks.complete(dbObjects.stream()
                        .map(dbObject -> MongoObjectConverter.toPojo(dbObject, Project.class).getTimeTracks())
                        .flatMap(Set::stream)
                        .collect(Collectors.toList()))
        );

        projectRepository.findAllByUserAndTracksTimeAgg(username, lastWeek).subscribe(subscriber);

        return retrievedTimeTracks.thenApplyAsync(this::convertToWeeklySumUpForTrackTypes);
    }

    Map<TrackType, Integer> convertToWeeklySumUpForTrackTypes(List<TimeTrack> timeTracks) {
        return timeTracks.stream()
                .collect(Collectors.groupingBy(TimeTrack::getType, Collectors.summingInt(TimeTrack::getDuration)));
    }

    public CompletableFuture<Map<String, Map<String, Integer>>> getWeeklySumUpForProjects(String username) {
        CompletableFuture<List<Project>> retrievedProjects = new CompletableFuture<>();

        LocalDate lastWeek = LocalDate.now().minusDays(LAST_WEEK);

        Subscriber<DBObject> subscriber = new ListDBObjectSubscriber(dbObjects ->
                retrievedProjects.complete(dbObjects.stream()
                        .map(dbObject -> MongoObjectConverter.toPojo(dbObject, Project.class))
                        .collect(Collectors.toList()))
        );

        projectRepository.findAllByUserAndTracksTimeAgg(username, lastWeek).subscribe(subscriber);

        return retrievedProjects.thenApplyAsync(this::convertToSumUpObject).thenApplyAsync(this::convertToWeeklySumUpForProjects);
    }

    Map<String, Map<LocalDate, Integer>> convertToSumUpObject(List<Project> projects) {
        return projects.stream()
                .map(this::convertToSumUpObject)
                .collect(Collectors.toMap(ProjectWeeklySumUp::getName, ProjectWeeklySumUp::getSummedTimeTracks));
    }

    ProjectWeeklySumUp convertToSumUpObject(Project project) {

        return new ProjectWeeklySumUp(project.getName(), project.getTimeTracks().stream()
                .collect(Collectors.groupingBy(TimeTrack::getDayOfIssue, Collectors.summingInt(TimeTrack::getDuration))));

    }

    class ProjectWeeklySumUp {
        private String name;
        private Map<LocalDate, Integer> summedTimeTracks;

        ProjectWeeklySumUp(String name, Map<LocalDate, Integer> summedTimeTracks) {
            this.name = name;
            this.summedTimeTracks = summedTimeTracks;
        }

        String getName() {
            return name;
        }

        Map<LocalDate, Integer> getSummedTimeTracks() {
            return summedTimeTracks;
        }
    }

    Map<String, Map<String, Integer>> convertToWeeklySumUpForProjects(Map<String, Map<LocalDate, Integer>> projectSumUps) {
        LocalDate startOfTheWeek = LocalDate.now().minusDays(LAST_WEEK);
        LocalDate endDate = LocalDate.now();
        Set<LocalDate> lastWeekDays = Stream.iterate(startOfTheWeek, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startOfTheWeek, endDate) + 1)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<String, Map<String, Integer>> weeklySumUpForProjects = new LinkedHashMap<>();

        lastWeekDays.forEach(date -> {
            String formattedDate = date.format(FORMATTER);
            if (!weeklySumUpForProjects.containsKey(formattedDate)) {
                weeklySumUpForProjects.put(formattedDate, new LinkedHashMap<>());
            }
            projectSumUps.forEach((s, localDateIntegerMap) -> weeklySumUpForProjects.get(formattedDate)
                    .put(s, localDateIntegerMap.getOrDefault(date, 0)));
        });

        return weeklySumUpForProjects;
    }
}
