package com.example

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class UrlShortenerSimulation extends Simulation {
  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")

  val scn = scenario("Shorten URL")
    .exec(http("Shorten URL")
      .post("/shorten")
      .body(StringBody("""{"fullUrl": "https://example.com/very/long/url", "customAlias": "test${randomUUID()}"}""")).asJson
      .check(status.is(201)))
    .pause(1)
    .exec(http("Redirect")
      .get("/test${randomUUID()}")
      .check(status.in(302, 404)))

  setUp(
    scn.inject(
      constantUsersPerSec(10) during (30 seconds)
    )
  ).protocols(httpProtocol)
}
