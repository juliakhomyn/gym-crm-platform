@component @workload
Feature: Trainer Workload Management

  Background:
    Given a registered user with username "Callum.Whitfield" and password "password222"

  Scenario: Add workload
    Given the user is authenticated
    When workload update is received:
      | action   | ADD        |
      | duration | 60         |
      | date     | 2026-01-15 |
    Then the response status is 200
    And trainer has 60 minutes in "JANUARY" 2026

  Scenario: Delete workload
    Given the user is authenticated
    And existing workload of 60 minutes in "JANUARY" 2026
    When workload update is received:
      | action   | DELETE     |
      | duration | 60         |
      | date     | 2026-01-15 |
    Then the response status is 200
    And trainer has 0 minutes in "JANUARY" 2026

  Scenario: Add workload with negative value fails
    Given the user is authenticated
    When workload update is received:
      | action   | DELETE     |
      | duration | -1         |
      | date     | 2026-01-15 |
    Then the response status is 400
    And trainer has 0 minutes in "JANUARY" 2026

  Scenario: Get workload returns info successfully
    Given the user is authenticated
    When workload update is received:
      | action   | ADD        |
      | duration | 60         |
      | date     | 2026-01-15 |
    Then the response status is 200
    When I request workload for user in "JANUARY" 2026
    Then the response status is 200
    And trainer has 60 minutes in "JANUARY" 2026

  Scenario: Unauthorized workload request
      When I request workload without authentication
      Then the response status is 401