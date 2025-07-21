# URL Shortener

A simple URL shortener application with a Micronaut backend, React frontend, Cucumber acceptance tests, and Gatling load
tests.

## Prerequisites

- Java 21
- Node.js 18+
- Docker
- Gradle

## Building and Running Locally

1. **Clone the repository**:
   ```bash
   git clone <your-forked-repo-url>
   cd url-shortener
   ```

2. **Build the project**:
   ```bash
   ./gradlew build
   ```

3. **Run with Docker Compose**:
   ```bash
   docker-compose up --build
   ```

4. **Access the application**:
    - Frontend UI: `http://localhost:5173`
    - Backend API: `http://localhost:8080`
    - MongoDB: `mongodb://localhost:27017`

5. **To run the acceptance tests**:
   Currently a stripped down set of example cucumber tests that would be run against a deployed testing/staging
   environment.
   These would cover the essential business happy path use cases it would also have a selenium/webdriver ui test.
    - Run it through your IDE starting the ``RunAcceptanceTests`` in ``tests/acceptance/src/main/java/com/example``
      The working directory should be set to ``tests/acceptance``

   *This would be packaged to be ran in the pipeline*
6. **To run the smoke tests**:
   These are the same as the same tests as the acceptance tests at the moment but in would be used in the cd pipelines
   to validate canary/prod deployments
    - Run it through your IDE starting the ``RunSmokeTests`` in ``tests/acceptance/src/main/java/com/example``
      The working directory should be set to ``tests/acceptance``
      *This would be packaged to be ran in the pipeline*
7. **To run NFTs**:
   This is an example nft that could be run in the pipeline to check latency,load
   - Run it through your IDE starting the ``RunSims`` in `tests/nft/src/main/java/com/example` 
## Example Usage

### Via UI

1. Open `http://localhost:5173` in your browser.
2. Enter a full URL (e.g., `https://example.com/very/long/url`) and an optional custom alias (e.g., `my-custom-alias`).
3. Click "Shorten" to generate a shortened URL.
4. Use the shortened URL (e.g., `http://localhost:8080/my-custom-alias`) to redirect to the original URL.

### Via API

- **Shorten a URL**:
  ```bash
  curl -X POST http://localhost:8080/shorten -H "Content-Type: application/json" -d '{"fullUrl": "https://example.com/very/long/url", "customAlias": "my-custom-alias"}'
  ```
  Response: `{"shortUrl": "http://localhost:8080/my-custom-alias"}`

- **List all URLs**:
  ```bash
  curl http://localhost:8080/urls
  ```
  Response: `[{"alias": "my-custom-alias", "fullUrl": "https://example.com/very/long/url", "shortUrl": "http://localhost:8080/my-custom-alias"}]`

- **Delete a URL**:
  ```bash
  curl -X DELETE http://localhost:8080/my-custom-alias
  ```

## Developing
1. Start mongodb by running ``docker compose mongodb up``
2. Start the frontend by running ``npm install && npm start`` in the `frontend` directory
3. Start the backend by running Application in ``backend/src/main/java/com/example``

## Notes and Assumptions
- Uses MongoDB for persistence.  (Data is lost on mongodb restart, in higher environments you'd have it setup properly)
- Backend is implemented with Micronaut and Java 21.
- Frontend is a simple React app with Tailwind CSS.
- Acceptance tests use Cucumber with JUnit.
- Load tests use Gatling.
- Custom aliases must be unique; duplicates return a 400 error.

## What would make this prod ready
In the interest of keeping time usage reasonable the following was skipped:
1.  not all tests that would be done for production have been done but I've
   covered a fairly comprehensive spike across the types of testing.
   **The most obvious thing missing is unhappy paths with downstream database, and what to do if a database is struggling**
2. Some of the modules have hard coded hosts/ports which in practice would need to be sourced from config
3. Add healthchecks of the appropriate types
4. prometheus metrics endpoints with the support alerting(alert-manager + downstream notifiers), dashboards(grafana) infrastructure. (Or the equivalent setup)
5. openapi + swagger ui,
6. config endpoint with obfuscated secrets for smoke tests
7. MDC with json structured logging for ingestion probably following some schema
8. full CD pipeline with docker image promotion(I can demo one setup if required)
9. Security hardening cors restrictions/content-security-policies, etc
10. dependency scanning to make sure we're kept up to date ( could just use githubs dependabot) and/or something like veracode if we'd like to know as early as possible.
