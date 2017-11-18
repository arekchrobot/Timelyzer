package pl.ark.chr.timelyzer.rest;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import pl.ark.chr.timelyzer.config.Server;
import pl.ark.chr.timelyzer.util.AppProps;
import ratpack.http.client.ReceivedResponse;
import ratpack.test.embed.EmbeddedApp;
import ratpack.test.http.TestHttpClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

public class TestEndpointTest {

    private EmbeddedApp prepareServer() {
        SimpleTestUsernamePasswordAuthenticator authenticator = new SimpleTestUsernamePasswordAuthenticator();
        Server server = new Server(authenticator, new TestEndpoint());
        return EmbeddedApp.fromServer(server.getRatpackServer());
    }

    private String getAuthToken(TestHttpClient testHttpClient) throws IOException {
        final ReceivedResponse receivedResponse = testHttpClient.get("/auth?username=test&password=test");

        assertThat(receivedResponse.getStatusCode()).isEqualTo(200);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(receivedResponse.getBody().getText());
        return node.get("token").asText();
    }

    @Test
    public void shouldReturn401() throws Exception {
        prepareServer().test(testHttpClient -> {
            final ReceivedResponse response = testHttpClient.requestSpec(rs ->
                    rs.headers(mh -> mh.add("Content-type", "application/json")))
                    .get("/api/test/get/1");

            assertThat(response.getStatusCode()).isEqualTo(401);
            assertThat(response.getBody().getText()).isNotEmpty().isEqualTo("401 ERROR");
        });
    }

    @Test
    public void shouldRenderHelloText() throws Exception {
        prepareServer().test(testHttpClient -> {
            final String authToken = getAuthToken(testHttpClient);

            final ReceivedResponse response = testHttpClient.requestSpec(rs ->
                    rs.headers(mh -> mh.add(AppProps.instance().getAuthHeader(), AppProps.instance().getAuthPrefixHeader() + authToken)))
                    .get("/api/test/get/2");

            assertThat(response.getStatusCode()).isEqualTo(200);
            assertThat(response.getBody().getText()).isNotEmpty().isEqualTo("Hello 2 test");
        });
    }

    @Test
    public void shouldGetJson() throws Exception {
        prepareServer().test(testHttpClient -> {
            final String authToken = getAuthToken(testHttpClient);

            final ReceivedResponse response = testHttpClient.requestSpec(rs ->
                    rs.headers(mh -> mh.add(AppProps.instance().getAuthHeader(), AppProps.instance().getAuthPrefixHeader() + authToken)))
                    .get("/api/test/getJson");

            assertThat(response.getStatusCode()).isEqualTo(200);
            assertThat(response.getBody().getText()).isNotEmpty();
        });
    }

    @Test
    public void shouldPostProjectAndProperlyParse() throws Exception {
        prepareServer().test(testHttpClient -> {
            final String authToken = getAuthToken(testHttpClient);

            final String postBody = "{\"id\":{\"$oid\":\"5a1041228055fe142868fc1c\"},\"users\":null,\"name\":null,\"timeTracks\":null}";

            final ReceivedResponse response = testHttpClient.requestSpec(rs ->
                    rs.headers(mh -> mh.add(AppProps.instance().getAuthHeader(), AppProps.instance().getAuthPrefixHeader() + authToken))
                            .body(body -> body.text(postBody)))
                    .post("/api/test/post");

            assertThat(response.getStatusCode()).isEqualTo(200);
            assertThat(response.getBody().getText()).isNotEmpty().isEqualTo("OK");
        });
    }
}
