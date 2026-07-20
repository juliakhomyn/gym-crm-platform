@component @auth
Feature: Authentication

  Background:
    Given a registered trainer with:
      | firstName      | Callum    |
      | lastName       | Whitfield |
      | specialization | Yoga      |

  @positive
  Scenario: Successful login
    When the user logs in with valid credentials
    Then the response status is 200
    And a JWT access token is returned
    And the response contains the authenticated username

  @negative
  Scenario: Login with incorrect password
    When the user logs in with an incorrect password
    Then the response status is 401

  @negative
  Scenario: Login with unknown username
    When the user logs in with username "Non.Existent" and password "password123"
    Then the response status is 404

  @positive
  Scenario: Logout
    Given the user is authenticated
    When the user logs out
    Then the response status is 200

  @negative
  Scenario: Logged out token cannot be reused
    Given the user is authenticated
    When the user logs out
    And the user accesses a protected endpoint with the same token
    Then the response status is 401

  @negative @edge @validation
  Scenario: Login with empty username
    When the user logs in with username "" and password "password123"
    Then the response status is 400

  @negative @edge @validation
  Scenario: Login with empty password
    When the user logs in with username "Callum.Whitfield" and password ""
    Then the response status is 400