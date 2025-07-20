Feature: URL Shortener

  Scenario: Shorten a URL with a custom alias
    Given I have a full URL "https://example.com/very/long/url"
    When I shorten it with custom alias "my-custom-alias"
    Then I should get a shortened URL "http://localhost:8080/my-custom-alias"
    And the shortened URL should redirect to "https://example.com/very/long/url"

  Scenario: Delete a shortened URL
    Given a shortened URL exists with alias "my-custom-alias"
    When I delete the shortened URL
    Then the shortened URL should no longer exist
