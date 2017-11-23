package pl.ark.chr.timelyzer.project;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.mockito.Mockito;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import pl.ark.chr.timelyzer.config.Server;
import pl.ark.chr.timelyzer.persistence.Project;
import pl.ark.chr.timelyzer.util.AppProps;
import ratpack.http.client.ReceivedResponse;
import ratpack.test.embed.EmbeddedApp;
import util.AuthorizationUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


public class ProjectEndpointTest {

    private CompletableFuture<List<Project>> responseFromService;

    private EmbeddedApp prepareServer() {
        SimpleTestUsernamePasswordAuthenticator authenticator = new SimpleTestUsernamePasswordAuthenticator();
        ProjectService service = Mockito.mock(ProjectService.class);
        when(service.getAllProjectsForUser(any(String.class))).thenReturn(responseFromService);
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
}