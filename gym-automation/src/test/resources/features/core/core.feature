@component @trainee
Feature: Trainee Management

  @positive
  Scenario: Register a new trainee successfully
    When the client registers a user with:
      | firstName | Cillian     |
      | lastName  | Mercer      |
      | birthDate | 1995-05-10  |
      | address   | Main Street |
    Then the response status is 200
    And the response contains generated username

  @negative @validation
  Scenario: Registration fails when required fields are missing
    When the client registers a user with:
      | firstName |             |
      | lastName  | Mercer      |
    Then the response status is 400

  @positive @edge
  Scenario: Register trainee with minimum required fields
    When the client registers a user with:
      | firstName | Nora       |
      | lastName  | Pemberton  |
    Then the response status is 200

  @positive @edge
  Scenario: Register trainers with the same name
    When the client registers a user with:
      | firstName | Nora      |
      | lastName  | Pemberton |
    Then the response status is 200
    When the client registers a user with:
      | firstName | Nora      |
      | lastName  | Pemberton |
    Then the response status is 200

  @negative @edge @validation
  Scenario: Registration fails with empty request
    When empty registration request is received
    Then the response status is 400