# Banking Platform Assignment

## What is implemented

- Multi-bank account handling using account format: `<BANK>-<NUMBER>`
  - Supported banks: `HDFC`, `ICICI`, `SBI`
- Account creation
- Account inquiry
- Deposit
- Withdrawal
- Fund transfer
  - Same-bank transfer
  - Cross-bank transfer
- Transaction history (latest transaction for account)
- Transfer failure handling with compensation (refund source account)

## APIs

Base path: `/api/transactionManager`

- `POST /account/create`
- `GET /account/inquire/{accountNumber}`
- `POST /account/deposit`
- `POST /account/withdraw`
- `POST /account/transfer`
- `GET /account/history/{accountNumber}`

## Request models (from transaction-manager-spec)

- Deposit/withdraw (`TransactionRequestDTO`)
  - `accountNumber`
  - `amount`
  - `CrDrIndicator` (`CREDIT` / `DEBIT`)

- Transfer (`TransferRequestDTO`)
  - `sourceAccountNumber`
  - `destinationAccountNumber`
  - `amount`

## Service architecture

Services implemented under `services/impl` using spec interfaces:

- `AccountServiceImpl` implements `AccountService`
- `CreditServiceImpl` implements `CreditService`
- `DebitServiceImpl` implements `DebitService`
- `FundTransferServiceImpl` implements `FundTransferService`
- `TranInquiryServiceImpl` implements `TranInquiryService`

## Design patterns used

- Command Pattern
  - `MoneyOperationCommand`
  - `DepositMoneyCommand`
  - `WithdrawMoneyCommand`
  - `MoneyOperationProcessor`
  - Purpose: encapsulate deposit/withdraw behavior in independent commands.

## Failure handling in transfer

`FundTransferServiceImpl` performs transfer as:

1. Debit source account
2. Credit destination account
3. If destination update fails, compensate by restoring source balance
4. Store transfer record as `COMPENSATED` or `FAILED`

## Unit testing (JUnit 5)

- `AccountServiceUtilTest`
  - Valid account mapping and bank resolution
  - Invalid enum validation

Run tests:

```bash
./mvnw test
```
