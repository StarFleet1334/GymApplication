Feature: Training Type Management

  Scenario Outline: Create a valid training type
    When the administrator requests to create a training type with name "<trainingTypeName>"
    Then the system should create the training type and return a success message

    Examples:
      | trainingTypeName   |
      | Java Fundamentals  |
      | Advanced Python    |

  Scenario: Create a training type without a name
    When the administrator requests to create a training type with no name
    Then the system should fail to create the training type and return an error message

  Scenario: Retrieve all training types when none exist
    When the administrator requests to retrieve all training types when none
    Then the system should return a no content status

  Scenario: Retrieve all training types when they exist
    When the administrator requests to retrieve all training types when exist
    Then the system should return all training types

  Scenario: Retrieve a training type by valid ID
    And there exists a training type with ID 1
    When the administrator requests to retrieve the training type by ID 1
    Then the system should return the training type details

  Scenario: Retrieve a training type by invalid ID
    When the administrator requests to retrieve a training type by ID 99
    Then the system should return an error stating the training type is not found
