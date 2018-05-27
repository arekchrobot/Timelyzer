package pl.ark.chr.timelyzer.factory;

import pl.ark.chr.timelyzer.project.ProjectEndpoint;
import pl.ark.chr.timelyzer.project.ProjectService;

public class EndpointFactory {

    public static ProjectEndpoint projectEndpoint() {
        return new ProjectEndpoint(ServiceFactory.projectService());
    }

    public static ProjectEndpoint projectEndpoint(ProjectService projectService) {
        return new ProjectEndpoint(projectService);
    }
}
