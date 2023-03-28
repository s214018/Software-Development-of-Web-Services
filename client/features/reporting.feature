#/**
#*
#* @author Muaz Ahmed & Hjalte Bøgehave
#*
#*/

Feature: Reporting

  Background:
    Given a registered customer with bank account with balance 1000
    And a registered merchant with bank account with balance 1000
    And the customer requested 4 tokens
    When a payment is made for 100
    Then the customer balance is 900 kr
    And the merchant balance is 1100 kr

  @DeleteAfterReporting
  Scenario: Customer report request
    When the customer requests report
    Then customer purchases should have 1 entry
    And merchant id is in the customer response

  @DeleteAfterReporting
  Scenario: Merchant report request
    When the merchant requests report
    Then merchant payments should have 1 entries
    And customer token is in the response

  @DeleteAfterReporting
  Scenario: Manager report request
    When the manager requests report
    Then manager report should contain the payment entry