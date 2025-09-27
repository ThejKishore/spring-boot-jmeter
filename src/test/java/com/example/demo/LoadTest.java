package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static us.abstracta.jmeter.javadsl.JmeterDsl.*;

/**
 * Simple load test using JMeter Java DSL to exercise the employee API endpoints.
 */
public class LoadTest {

    /**
     * Generates a random string of the given length from the provided alphabet.
     *
     * @param alphabet source characters to choose from
     * @param len desired length of the random string
     * @return generated random string
     */
    private static String randomFrom(String alphabet, int len) {
        StringBuilder sb = new StringBuilder(len);
        ThreadLocalRandom r = ThreadLocalRandom.current();
        for (int i = 0; i < len; i++) {
            sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

    /**
     * Runs a small load test plan exercising create, get by id, and list endpoints.
     *
     * @throws IOException if the JMeter HTML report cannot be written
     */
    @Test
    public void testLoad() throws IOException {
        String baseUrl = System.getProperty("targetBaseUrl", "http://localhost:8080");

        // Build request body with Jackson
        Map<String, Object> body = new HashMap<>();
        body.put("name", "name_" + randomFrom("abcdef0123456789", 8));
        body.put("email", "email_" + randomFrom("abcdef0123456789", 6) + "@example.com");
        body.put("phone", randomFrom("0123456789", 10));
        String jsonBody = new ObjectMapper().writeValueAsString(body);

        testPlan(
            httpDefaults()
                .url(baseUrl),
            threadGroup(10, 10,
                // Default headers for JSON API
                httpHeaders()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json"),

                // Create an employee
                httpSampler("Create Employee","/employee")
                    .method("POST")
                    .body(jsonBody)
                    .contentType(org.apache.http.entity.ContentType.APPLICATION_JSON)
                    .children(
                        // Extract the generated id from JSON response (JMESPath: use 'id' not '$.id')
                        jsonExtractor("empId", "id")
                    ),

                // Pause 500 ms before next request
//                constantTimer(Duration.ofMillis(500)),

                // Display and validate the extracted id before requesting by id
                jsr223Sampler("Display & validate empId",
                        "def id = vars.get('empId');\n" +
                        "println('[DEBUG_LOG] Extracted empId=' + id);\n" +
                        "if (id == null || !(id ==~ /\\d+/)) { throw new AssertionError('empId is invalid: ' + id) }\n"
                ),

                // Fetch by id
                httpSampler("Fetch employee by id","/employee/${empId}"),

                // Fetch all
                httpSampler("Fetch all employees","/employee")

            ),
            htmlReporter("build/reports/jmeter")
        ).run();
    }
}
