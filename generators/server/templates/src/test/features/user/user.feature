Feature: User management

    Scenario: Retrieve administrator user
        When I search user '3'
        Then the user is found
        And his last name is 'Administrator'
