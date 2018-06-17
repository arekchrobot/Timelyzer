package pl.ark.chr.timelyzer.project;

import pl.ark.chr.timelyzer.filters.SameUserAccessHandler;
import pl.ark.chr.timelyzer.rest.RestEndpoint;
import ratpack.exec.Promise;
import ratpack.func.Action;
import ratpack.handling.Chain;

import static ratpack.jackson.Jackson.json;

public class ProjectEndpoint implements RestEndpoint {

    private ProjectService projectService;

    public ProjectEndpoint(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public String getApiPrefix() {
        return "project";
    }

    @Override
    public Action<Chain> defineActions() {
        return chain -> chain
                .all(new SameUserAccessHandler())
                .get("byUsername", handler ->
                        Promise.async(downstream -> downstream.accept(projectService.getAllProjectsForUser(handler
                                        .getRequest()
                                        .getQueryParams()
                                        .get("username")
                                ))
                        ).then(projects -> handler.render(json(projects)))
                )
                .get("weeklySumUpTracks", handler ->
                        Promise.async(downstream -> downstream.accept(projectService.getWeeklySumUpForTrackTypes(handler
                                        .getRequest()
                                        .getQueryParams()
                                        .get("username")
                                ))
                        ).then(sumUp -> handler.render(json(sumUp)))
                )
                .get("weeklySumUpProjects", handler ->
                        Promise.async(downstream -> downstream.accept(projectService.getWeeklySumUpForProjects(handler
                                        .getRequest()
                                        .getQueryParams()
                                        .get("username")
                                ))
                        ).then(sumUp -> handler.render(json(sumUp)))
                );
    }
}
