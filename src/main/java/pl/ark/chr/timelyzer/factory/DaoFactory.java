package pl.ark.chr.timelyzer.factory;

import pl.ark.chr.timelyzer.repository.ProjectRepository;
import pl.ark.chr.timelyzer.repository.UserRepository;

public class DaoFactory {

    public static UserRepository userRepository() {
        return new UserRepository();
    }

    public static ProjectRepository projectRepository() {
        return new ProjectRepository();
    }
}
