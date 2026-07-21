@component @integration @workload
Feature: Core to Workload Integration

  Background:
    Given a registered trainer with:
      | firstName      | Callum    |
      | lastName       | Whitfield |
      | specialization | Yoga      |
    And a registered trainee with:
      | firstName | Cillian     |
      | lastName  | Mercer      |
      | birthDate | 1995-05-10  |
      | address   | Main Street |
    And the user is authenticated

  @positive
  Scenario: Creating a training updates trainer workload
    When the client creates a training with:
      | trainingName     | Yoga       |
      | trainingDate     | 2026-01-15 |
      | trainingDuration | 60         |
    Then the response status is 200
    And trainer has 60 minutes in "JANUARY" 2026

  @positive
  Scenario: Deleting a trainee removes all related workload
    Given the trainee is authenticated
    And the trainee has training sessions
    When trainee is deleted
    Then the response status is 200
    And trainer has 0 minutes in "JANUARY" 2026

  @negative @validation
  Scenario: Invalid training request does not update workload
    When the client creates a training with:
      | trainingName     | Yoga       |
      | trainingDate     | 2026-01-15 |
      | trainingDuration | -60        |
    Then the response status is 400
    When I request workload for user in "JANUARY" 2026
    Then the response status is 404

  @negative @security
  Scenario: Unauthenticated user cannot create training
    Given the user is not authenticated
    When the client creates a training with:
      | trainingName     | Yoga       |
      | trainingDate     | 2026-07-20 |
      | trainingDuration | 60         |
    Then the response status is 401