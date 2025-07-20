package com.example;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.Duration;
import java.util.UUID;

public class UrlShortenerSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080") // Would come from config
            .acceptHeader("application/json")
            .disableFollowRedirect();

    ScenarioBuilder scn = scenario("Shorten URL")
            .exec(session -> {
                String alias = "test" + UUID.randomUUID();
                return session.set("alias", alias);
            })
            .exec(http("Shorten URL")
                    .post("/shorten")
                    .body(StringBody(session -> "{\"fullUrl\": \"http://google.com/very/long/url\", \"customAlias\": \"" + session.getString("alias") + "\"}"))
                    .asJson()
                    .check(status().is(201)))
            .pause(Duration.ofSeconds(1))
            .exec(http("Redirect")
                    .get(session -> "/" + session.getString("alias"))
                    .check(status().in(301)));

    {
        setUp(
                scn.injectOpen(
                        constantUsersPerSec(10).during(Duration.ofSeconds(30)) // would come from config
                )
        ).protocols(httpProtocol)
        .assertions(
                global().responseTime().max().lte(100), // would come from config
                global().successfulRequests().percent().is(100.0)  // would come from config
        );
    }
}