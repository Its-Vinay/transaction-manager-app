Feature: Banking APIs

  Background:
    Given the database is clean

  Scenario: Create account API creates a new account
    When I call create account API with account number "HDFC-1001", name "John Doe", status "ACTIVE", freeze status "NOT_FROZEN"
    Then the response status is 201
    And the response field "accountNumber" equals "HDFC-1001"
    And the response field "status" equals "ACTIVE"

  Scenario: Account inquiry API returns account details
    Given an account exists with account number "ICICI-2001" and name "Jane Doe"
    When I call account inquiry API for account number "ICICI-2001"
    Then the response status is 200
    And the response field "accountNumber" equals "ICICI-2001"
    And the response field "accountName" equals "Jane Doe"

  Scenario: Deposit API credits balance and returns success
    Given an account exists with account number "SBI-3001" and name "Deposit User"
    When I call deposit API for account "SBI-3001" with amount "500.00"
    Then the response status is 200
    And the response field "status" equals "SUCCESS"
    And the response field "transactionId" is not blank
    And account "SBI-3001" balance should be "500.00"

  Scenario: Withdraw API debits balance and returns success
    Given an account exists with account number "HDFC-4001" and name "Withdraw User"
    And I call deposit API for account "HDFC-4001" with amount "700.00"
    And the response status is 200
    When I call withdraw API for account "HDFC-4001" with amount "250.00"
    Then the response status is 200
    And the response field "status" equals "SUCCESS"
    And the response field "transactionId" is not blank
    And account "HDFC-4001" balance should be "450.00"

  Scenario: Transfer API moves funds between accounts
    Given accounts exist for transfer from "HDFC-5001" to "ICICI-5002"
    And I call deposit API for account "HDFC-5001" with amount "1000.00"
    And the response status is 200
    When I call transfer API from "HDFC-5001" to "ICICI-5002" with amount "300.00"
    Then the response status is 200
    And the response field "status" equals "SUCCESS"
    And the response field "transactionId" is not blank
    And account "HDFC-5001" balance should be "700.00"
    And account "ICICI-5002" balance should be "300.00"

  Scenario: Transaction history API returns latest transaction
    Given an account exists with account number "SBI-6001" and name "History User"
    And I call deposit API for account "SBI-6001" with amount "125.00"
    And the response status is 200
    When I call transaction history API for account number "SBI-6001"
    Then the response status is 200
    And the response field for retrieve transaction "transactionType" equals "CREDIT"
    And the response field for retrieve transaction "transactionStatus" equals "SUCCESS"
