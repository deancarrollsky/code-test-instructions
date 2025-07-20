package com.example;
import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;

public class RunSims {
    public static void main(String[] args) {
        GatlingPropertiesBuilder props = new GatlingPropertiesBuilder();

        props.simulationClass("com.example.UrlShortenerSimulation");

        // Optionally set results directory
        props.resultsDirectory("results");

        // Launch Gatling
        Gatling.fromMap(props.build());
    }
}
