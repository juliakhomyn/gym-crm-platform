@component @core
Feature: Trainee Management

  Scenario: Register a new trainee successfully
    When the client registers a trainee with:
      | firstName | Cillian     |
      | lastName  | Mercer      |
      | birthDate | 1995-05-10  |
      | address   | Main Street |
    Then the response status is 200
    And the response contains generated username
    And trainee first name is Cillian
    And trainee last name is Mercer

  Scenario: Registration fails when required fields are missing
    When the client registers a trainee with:
      | firstName |             |
      | lastName  | Mercer      |
    Then the response status is 400