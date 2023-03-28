# @author Mads Klynderud (s215955)
Feature: Token Creation

  Scenario: Create tokens for a customer with no previous tokens
    Given a customer with random UUID and bank account
    When the customer requests 1 tokens
    Then 1 tokens is returned

  Scenario: Create 6 tokens for a customer
    Given a customer with random UUID and bank account
    When the customer requests 6 tokens
    Then bad request error is returned

  Scenario: Create tokens when customer has more than 1 token
    Given a customer with random UUID and bank account
    And the customer requests 3 tokens
    When the customer requests 2 tokens
    Then bad request error is returned