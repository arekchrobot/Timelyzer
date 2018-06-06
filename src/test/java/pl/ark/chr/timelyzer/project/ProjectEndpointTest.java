package pl.ark.chr.timelyzer.project;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.mockito.Mockito;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import pl.ark.chr.timelyzer.config.Server;
import pl.ark.chr.timelyzer.persistence.Project;
import pl.ark.chr.timelyzer.persistence.TrackType;
import pl.ark.chr.timelyzer.util.AppProps;
import ratpack.http.client.ReceivedResponse;
import ratpack.test.embed.EmbeddedApp;
import util.AuthorizationUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


public class ProjectEndpointTest {

    private CompletableFuture<List<Project>> responseFromService;
    private CompletableFuture<Map<TrackType, Integer>> responseFromTracks;
    private CompletableFuture<Map<String, Map<String, Integer>>> responseFromProjects;

    private EmbeddedApp prepareServer() {
        SimpleTestUsernamePasswordAuthenticator authenticator = new SimpleTestUsernamePasswordAuthenticator();
        ProjectService service = Mockito.mock(ProjectService.class);
        when(service.getAllProjectsForUser(any(String.class))).thenReturn(responseFromService);
        when(service.getWeeklySumUpForTrackTypes(any(String.class))).thenReturn(responseFromTracks);
        when(service.getWeeklySumUpForProjects(any(String.class))).thenReturn(responseFromProjects);
        Server server = new Server(authenticator, new ProjectEndpoint(service));
        return EmbeddedApp.fromServer(server.getRatpackServer());
    }

    @Test
    public void shouldReturn401() throws Exception {
        prepareServer().test(testHttpClient -> {
            final ReceivedResponse response = testHttpClient.requestSpec(rs ->
                    rs.headers(mh -> mh.add("Content-type", "application/json")))
                    .get("/api/project/byUsername?username=test");

            assertThat(response.getStatusCode()).isEqualTo(401);
            assertThat(response.getBody().getText()).isNotEmpty().isEqualTo("401 ERROR");
        });
    }

    @Test
    public void shouldReturnArrayForUser() throws Exception {
        String expectedResult = "[{\"id\":{\"$oid\":\"5a171af5fe8f0ac220956566\"},\"users\":null,\"name\":\"TestProj\",\"timeTracks\":null}]";

        responseFromService = new CompletableFuture<>();

        prepareServer().test(testHttpClient -> {
            final String authToken = AuthorizationUtil.getAuthToken(testHttpClient);

            responseFromService.complete(Arrays.asList(Project.builder().id(new ObjectId("5a171af5fe8f0ac220956566")).name("TestProj").build()));

            final ReceivedResponse response = testHttpClient.requestSpec(rs ->
                    rs.headers(mh -> mh.add(AppProps.instance().getAuthHeader(), AppProps.instance().getAuthPrefixHeader() + authToken)))
                    .get("/api/project/byUsername?username=test");

            assertThat(response).isNotNull();
            assertThat(response.getBody().getText()).isNotEmpty().isEqualTo(expectedResult);
        });
    }

    @Test
    public void shouldReturnEmptyArrayWhenUserIsNotExisting() throws Exception {
        String expectedResult = "[]";

        responseFromService = new CompletableFuture<>();

        prepareServer().test(testHttpClient -> {
            final String authToken = AuthorizationUtil.getAuthToken(testHttpClient);

            responseFromService.complete(Collections.emptyList());

            final ReceivedResponse response = testHttpClient.requestSpec(rs ->
                    rs.headers(mh -> mh.add(AppProps.instance().getAuthHeader(), AppProps.instance().getAuthPrefixHeader() + authToken)))
                    .get("/api/project/byUsername?username=wrongTest");

            assertThat(response).isNotNull();
            assertThat(response.getBody().getText()).isNotEmpty().isEqualTo(expectedResult);
        });
    }

    @Test
    public void shouldReturnWeeklySumUpProjectsForUser() throws Exception {
        String expectedResult = "{" +
                "\"2018-05-29\":{\"Project2Test\":0,\"Project1Test\":0}," +
                "\"2018-05-30\":{\"Project2Test\":0,\"Project1Test\":0}," +
                "\"2018-05-31\":{\"Project2Test\":0,\"Project1Test\":0}," +
                "\"2018-06-01\":{\"Project2Test\":0,\"Project1Test\":0}," +
                "\"2018-06-02\":{\"Project2Test\":0,\"Project1Test\":3}," +
                "\"2018-06-03\":{\"Project2Test\":0,\"Project1Test\":0}," +
                "\"2018-06-04\":{\"Project2Test\":5,\"Project1Test\":0}," +
                "\"2018-06-05\":{\"Project2Test\":0,\"Project1Test\":0}" +
                "}";

        responseFromProjects = new CompletableFuture<>();

        Map<String, Map<String, Integer>> responseProject = getProjectSumUp();

        prepareServer().test(testHttpClient -> {
            final String authToken = AuthorizationUtil.getAuthToken(testHttpClient);

            responseFromProjects.complete(responseProject);

            final ReceivedResponse response = testHttpClient.requestSpec(rs ->
                    rs.headers(mh -> mh.add(AppProps.instance().getAuthHeader(), AppProps.instance().getAuthPrefixHeader() + authToken)))
                    .get("/api/project/weeklySumUpProjects?username=test");

            assertThat(response).isNotNull();
            assertThat(response.getBody().getText()).isNotEmpty().isEqualTo(expectedResult);
        });
    }

    @Test
    public void shouldReturnEmptyWeeklySumUpProjectsForUser() throws Exception {
        String expectedResult = "{}";

        responseFromProjects = new CompletableFuture<>();

        prepareServer().test(testHttpClient -> {
            final String authToken = AuthorizationUtil.getAuthToken(testHttpClient);

            responseFromProjects.complete(Collections.emptyMap());

            final ReceivedResponse response = testHttpClient.requestSpec(rs ->
                    rs.headers(mh -> mh.add(AppProps.instance().getAuthHeader(), AppProps.instance().getAuthPrefixHeader() + authToken)))
                    .get("/api/project/weeklySumUpProjects?username=test");

            assertThat(response).isNotNull();
            assertThat(response.getBody().getText()).isNotEmpty().isEqualTo(expectedResult);
        });
    }

    @Test
    public void shouldReturnWeeklySumUpTracksForUser() throws Exception {
        String expectedResult = "{\"DEVELOPMENT\":5,\"RESEARCH\":3}";

        responseFromTracks = new CompletableFuture<>();

        Map<TrackType, Integer> responseTracks = new LinkedHashMap<>();
        responseTracks.put(TrackType.DEVELOPMENT, 5);
        responseTracks.put(TrackType.RESEARCH, 3);

        prepareServer().test(testHttpClient -> {
            final String authToken = AuthorizationUtil.getAuthToken(testHttpClient);

            responseFromTracks.complete(responseTracks);

            final ReceivedResponse response = testHttpClient.requestSpec(rs ->
                    rs.headers(mh -> mh.add(AppProps.instance().getAuthHeader(), AppProps.instance().getAuthPrefixHeader() + authToken)))
                    .get("/api/project/weeklySumUpTracks?username=test");

            assertThat(response).isNotNull();
            assertThat(response.getBody().getText()).isNotEmpty().isEqualTo(expectedResult);
        });
    }

    @Test
    public void shouldReturnEmptyWeeklySumUpTracksForUser() throws Exception {
        String expectedResult = "{}";

        responseFromTracks = new CompletableFuture<>();

        prepareServer().test(testHttpClient -> {
            final String authToken = AuthorizationUtil.getAuthToken(testHttpClient);

            responseFromTracks.complete(Collections.emptyMap());

            final ReceivedResponse response = testHttpClient.requestSpec(rs ->
                    rs.headers(mh -> mh.add(AppProps.instance().getAuthHeader(), AppProps.instance().getAuthPrefixHeader() + authToken)))
                    .get("/api/project/weeklySumUpTracks?username=test123");

            assertThat(response).isNotNull();
            assertThat(response.getBody().getText()).isNotEmpty().isEqualTo(expectedResult);
        });
    }

    private Map<String, Map<String, Integer>> getProjectSumUp() {
        Map<String, Map<String, Integer>> response = new LinkedHashMap<>();

        Map<String, Integer> day0529 = new LinkedHashMap<>();
        day0529.put("Project2Test", 0);
        day0529.put("Project1Test", 0);

        Map<String, Integer> day0530 = new LinkedHashMap<>();
        day0530.put("Project2Test", 0);
        day0530.put("Project1Test", 0);

        Map<String, Integer> day0531 = new LinkedHashMap<>();
        day0531.put("Project2Test", 0);
        day0531.put("Project1Test", 0);

        Map<String, Integer> day0601 = new LinkedHashMap<>();
        day0601.put("Project2Test", 0);
        day0601.put("Project1Test", 0);

        Map<String, Integer> day0602 = new LinkedHashMap<>();
        day0602.put("Project2Test", 0);
        day0602.put("Project1Test", 3);

        Map<String, Integer> day0603 = new LinkedHashMap<>();
        day0603.put("Project2Test", 0);
        day0603.put("Project1Test", 0);

        Map<String, Integer> day0604 = new LinkedHashMap<>();
        day0604.put("Project2Test", 5);
        day0604.put("Project1Test", 0);

        Map<String, Integer> day0605 = new LinkedHashMap<>();
        day0605.put("Project2Test", 0);
        day0605.put("Project1Test", 0);

        response.put("2018-05-29", day0529);
        response.put("2018-05-30", day0530);
        response.put("2018-05-31", day0531);
        response.put("2018-06-01", day0601);
        response.put("2018-06-02", day0602);
        response.put("2018-06-03", day0603);
        response.put("2018-06-04", day0604);
        response.put("2018-06-05", day0605);
        return response;
    }
}