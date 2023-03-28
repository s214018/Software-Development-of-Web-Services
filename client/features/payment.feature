#/**
#* @author Gustav Nilsson Pedersen - s174562
#* **/

Feature: Payment
  Payment end-to-end tests

  Background:
    Given a customer with a bank account with balance 1000
    And that the customer is registered with DTU Pay
    Given a merchant with a bank account with balance 2000
    And that the merchant is registered with DTU Pay

  @DeleteAccountsAfter
  Scenario: Successful Payment
    Given the customer has tokens
    When the merchant initiates a payment for 100 kr with the customer token
    Then the payment is successful
    And the balance of the customer at the bank is 900 kr
    And the balance of the merchant at the bank is 2100 kr

  @DeleteAccountsAfter
  Scenario: Invalid Token
    When the merchant initiates a payment for 100 kr with invalid token
    Then A bad request error is returned

  @DeleteAccountsAfter
  Scenario: Merchant not registered
    Given a merchant "Steefan" "Kaffka" with CPR "120045-0493" with a bank account with balance 1000
    And the customer has tokens
    When the unregistered merchant initiates a payment for 100 kr with the customer token
    Then A bad request error is returned