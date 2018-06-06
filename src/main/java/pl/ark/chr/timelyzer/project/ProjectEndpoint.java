package pl.ark.chr.timelyzer.project;

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

//    WEEKLY SUMUP:
//    {
//        "2018-05-29": {
//        "Project2Test": 0,
//                "Project1Test": 0
//    },
//        "2018-05-30": {
//        "Project2Test": 0,
//                "Project1Test": 0
//    },
//        "2018-05-31": {
//        "Project2Test": 0,
//                "Project1Test": 0
//    },
//        "2018-06-01": {
//        "Project2Test": 0,
//                "Project1Test": 0
//    },
//        "2018-06-02": {
//        "Project2Test": 0,
//                "Project1Test": 3
//    },
//        "2018-06-03": {
//        "Project2Test": 0,
//                "Project1Test": 0
//    },
//        "2018-06-04": {
//        "Project2Test": 5,
//                "Project1Test": 0
//    },
//        "2018-06-05": {
//        "Project2Test": 0,
//                "Project1Test": 0
//    }
//    }

//    WEEKLY PROJECTS:
//    {
//        "DEVELOPMENT": 5,
//            "RESEARCH": 3
//    }
}
