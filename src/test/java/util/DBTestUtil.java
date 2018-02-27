package util;

import java.time.LocalDate;
import java.time.ZoneId;

public class DBTestUtil {

    private static final ZoneId zone = ZoneId.systemDefault();

    public static final String testUser = "{\"_id\":\"test\",\"password\":\"$2a$11$n8IPQk8LfidgekWd/l9cHOc/UPA9GHjoMISeyWYDMIlIdfBH8PYBK\"}";
    public static final String testUser2 = "{\"_id\":\"test@gmail.com\",\"password\":\"$2a$11$n8IPQk8LfidgekWd/l9cHOc/UPA9GHjoMISeyWYDMIlIdfBH8PYBK\"}";

    public static final String testProject = "{\"_id\":{\"$oid\":\"5a171af5fe8f0ac220956566\"},\"users\":[\"test\"],\"name\":\"TestProj\",\"timeTracks\":null}";
    public static final String testProject2 = "{\"_id\":{\"$oid\":\"5a526ff4b9ec391993c94e1b\"}," +
            "\"name\":\"Project2Test\"," +
            "\"users\":[\"test@gmail.com\", \"test2@gmail.com\"]," +
            "\"timeTracks\": [" +
            "{\"dayOfIssue\": " + LocalDate.now().minusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() + ",\"user\":\"test@gmail.com\",\"comment\":\"Research\",duration: 3,type: \"RESEARCH\"}," +
            "{\"dayOfIssue\": " + LocalDate.now().minusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() + ",\"user\":\"test@gmail.com\",\"comment\":\"Implement\",duration: 2,type: \"DEVELOPMENT\"}," +
            "{\"dayOfIssue\": " + LocalDate.now().minusDays(8).atStartOfDay(zone).toInstant().toEpochMilli() + ",\"user\":\"test2@gmail.com\",\"comment\":\"Research\",duration: 8,type: \"DEVELOPMENT\"}" +
            "]}";
    public static final String testProject3 = "{\"_id\":{\"$oid\":\"5a5262deb9ec391993c94e1a\"}," +
            "\"name\":\"Project1Test\"," +
            "\"users\":[\"test@gmail.com\"]," +
            "\"timeTracks\": [" +
            "{\"dayOfIssue\": " + LocalDate.now().minusDays(3).atStartOfDay(zone).toInstant().toEpochMilli() + ",\"user\":\"test@gmail.com\",\"comment\":\"Research\",duration: 3,type: \"DEVELOPMENT\"}" +
            "]}";
}
