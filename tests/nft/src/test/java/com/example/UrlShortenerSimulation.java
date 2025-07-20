package com.example;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.Duration;

public class UrlShortenerSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json");

    ScenarioBuilder scn = scenario("Shorten URL")
            .exec(http("Shorten URL")
                    .post("/shorten")
                    .body(StringBody(session -> "{\"fullUrl\": \"https://example.com/very/long/url\", \"customAlias\": \"test\" + java.util.UUID.randomUUID().toString() + \"}"))
                    .asJson()
                    .check(status().is(201)))
            .pause(Duration.ofSeconds(1))
            .exec(http("Redirect")
                    .get(session -> "/test" + java.util.UUID.randomUUID().toString())
                    .check(status().in(302, 404)));

    {
        setUp(
                scn.injectOpen(
                        constantUsersPerSec(10).during(Duration.ofSeconds(30))
                )
        ).protocols(httpProtocol);
    }
}