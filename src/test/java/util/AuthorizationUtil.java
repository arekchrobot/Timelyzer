package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ratpack.http.client.ReceivedResponse;
import ratpack.test.http.TestHttpClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthorizationUtil {

    public static String getAuthToken(TestHttpClient testHttpClient) throws IOException {
        final ReceivedResponse receivedResponse = testHttpClient.get("/auth?username=test&password=test");

        assertThat(receivedResponse.getStatusCode()).isEqualTo(200);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(receivedResponse.getBody().getText());
        return node.get("token").asText();
    }
}
