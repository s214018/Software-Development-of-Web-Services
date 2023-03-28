#/**
#*
#* @author Julia Makulec
#*
#*/
Feature: Registration
  Tests of Account Service
  Scenario: New customer registered successfully
    Given a bank account with ID "5869a418-9277-11ed-a1eb-0242ac120333"
    When a new customer is created with the bank account
    Then the request for a new customer has succeded

  Scenario: New customer registration attempted with empty bank account id
    Given a bank account with ID ""
    When a new customer registration is attempted
    Then the response returns bad request

  Scenario: New merchant registered successfully
    Given a bank account with ID "4569a418-9277-11ed-a1eb-0242ac120333"
    When a new merchant is created with the bank account
    Then the request for a new merchant has succeded

  Scenario: New merchant registration attempted with empty bank account id
    Given a bank account with ID ""
    When a new merchant registration is attempted
    Then the response returns bad request

  Scenario: An existing merchant requests deregistration
    Given a bank account with ID "4569a418-9277-11ed-a1eb-0242acsdsds3"
    And a new merchant is created with the bank account
    When the merchant requests deregistration
    Then the request deregistering merchant is successful

  Scenario: A non-registered merchant requests deregistration
    When a merchant with ID "0e4bbc0e-ce1d-49f6-9702-e4e074290513" requests deregistration
    Then the response returns bad request

  Scenario: An existing customer requests deregistration
    Given a bank account with ID "testcustomerbankid"
    And a new customer is created with the bank account
    When the customer requests deregistration
    Then the request deregistering customer is successful

  Scenario: A non-registered customer requests deregistration
    When a customer with random ID requests deregistration
    Then the response returns bad request

  @DeleteAccountsAfter
  Scenario: A new bank account is created successfully
    When I register a bank account with balance 1000
    Then The bank account exists
    And The bank account has balance 1000

