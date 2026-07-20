@component @workload
Feature: Trainer Workload Management

  Background:
    Given a registered trainer with:
      | firstName      | Callum    |
      | lastName       | Whitfield |
      | specialization | Yoga      |

  @positive
  Scenario: Add workload
    Given the user is authenticated
    When workload update is received:
      | actionType       | ADD        |
      | trainingDuration | 60         |
      | trainingDate     | 2026-01-15 |
    Then the response status is 200
    And trainer has 60 minutes in "JANUARY" 2026

  @positive
  Scenario: Delete workload
    Given the user is authenticated
    And existing workload of 60 minutes in "JANUARY" 2026
    When workload update is received:
      | actionType       | DELETE     |
      | trainingDuration | 30         |
      | trainingDate     | 2026-01-15 |
    Then the response status is 200
    And trainer has 30 minutes in "JANUARY" 2026

    @positive @edge
    Scenario: Delete entire workload
      Given the user is authenticated
      And existing workload of 60 minutes in "JANUARY" 2026
      When workload update is received:
        | actionType       | DELETE     |
        | trainingDuration | 60         |
        | trainingDate     | 2026-01-15 |
      Then the response status is 200
      And trainer has 0 minutes in "JANUARY" 2026

  @negative @validation
  Scenario: Invalid workload update does not create workload
    Given the user is authenticated
    When workload update is received:
      | actionType       | DELETE     |
      | trainingDuration | -1         |
      | trainingDate     | 2026-01-15 |
    Then the response status is 400
    When I request workload for user in "JANUARY" 2026
    Then the response status is 404

  @positive
  Scenario: Get workload returns info successfully
    Given the user is authenticated
    When workload update is received:
      | actionType       | ADD        |
      | trainingDuration | 60         |
      | trainingDate     | 2026-01-15 |
    Then the response status is 200
    When I request workload for user in "JANUARY" 2026
    Then the response status is 200
    And trainer has 60 minutes in "JANUARY" 2026

  @negative
  Scenario: Unauthorized workload request
    When I request workload without authentication
    Then the response status is 401

  @negative @edge @validation
  Scenario: Workload update with empty request
    Given the user is authenticated
    When an empty workload update is received
    Then the response status is 400