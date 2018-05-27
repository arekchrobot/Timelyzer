package pl.ark.chr.timelyzer.factory;

import pl.ark.chr.timelyzer.auth.MongoUsernamePasswordAuthenticator;
import pl.ark.chr.timelyzer.project.ProjectService;
import pl.ark.chr.timelyzer.repository.ProjectRepository;
import pl.ark.chr.timelyzer.repository.UserRepository;

public class ServiceFactory {

    public static ProjectService projectService() {
        return new ProjectService(DaoFactory.projectRepository());
    }

    public static ProjectService projectService(ProjectRepository projectRepository) {
        return new ProjectService(projectRepository);
    }

    public static MongoUsernamePasswordAuthenticator mongoUsernamePasswordAuthenticator() {
        return new MongoUsernamePasswordAuthenticator(DaoFactory.userRepository());
    }

    public static MongoUsernamePasswordAuthenticator mongoUsernamePasswordAuthenticator(UserRepository userRepository) {
        return new MongoUsernamePasswordAuthenticator(userRepository);
    }
}
