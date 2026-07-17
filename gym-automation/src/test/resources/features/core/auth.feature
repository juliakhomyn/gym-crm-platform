@component @auth
Feature: Authentication

  Background:
    Given a registered user with username "Callum.Whitfield" and password "password222"

  Scenario: Successful login
    When the user logs in with valid credentials
    Then the response status is 200
    And a JWT access token is returned
    And the response contains the authenticated username

  Scenario: Login with incorrect password
    When the user logs in with an incorrect password
    Then the response status is 401

  Scenario: Login with unknown username
    When the user logs in with username "Non.Existent" and password "password123"
    Then the response status is 404

  Scenario: Logout
    Given the user is authenticated
    When the user logs out
    Then the response status is 200

  Scenario: Logged out token cannot be reused
    Given the user is authenticated
    When the user logs out
    And the user accesses a protected endpoint with the same token
    Then the response status is 401