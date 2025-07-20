package com.example;

import io.cucumber.core.cli.Main;

public class RunSmokeTests {

    public static void main(String[] args) {
        // Define the arguments just like you'd pass them on the CLI
        String[] cucumberOptions = new String[] {
                "--glue", "com.example",
                "--tags", "@smoke",
                "src/main/resources/features",
                "--plugin", "pretty",
                "--plugin", "summary"
        };

        // Run Cucumber
        int exitStatus = Main.run(cucumberOptions, Thread.currentThread().getContextClassLoader());
        System.exit(exitStatus);
    }
}
