package com.springboot.transaction.cucumber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.transaction.entities.Account;
import com.springboot.transaction.repositories.AccountRepository;
import com.springboot.transaction.repositories.TransactionRecordRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class BankingApiStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    private MvcResult lastResult;

    @Given("the database is clean")
    public void theDatabaseIsClean() {
        transactionRecordRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Given("an account exists with account number {string} and name {string}")
    public void anAccountExistsWithAccountNumberAndName(String accountNumber, String accountName) throws Exception {
        performCreateAccount(accountNumber, accountName, "ACTIVE", "NOT_FROZEN");
        assertStatus(201);
    }

    @Given("accounts exist for transfer from {string} to {string}")
    public void accountsExistForTransfer(String sourceAccount, String destinationAccount) throws Exception {
        performCreateAccount(sourceAccount, "Source User", "ACTIVE", "NOT_FROZEN");
        assertStatus(201);
        performCreateAccount(destinationAccount, "Destination User", "ACTIVE", "NOT_FROZEN");
        assertStatus(201);
    }

    @When("I call create account API with account number {string}, name {string}, status {string}, freeze status {string}")
    public void iCallCreateAccountApi(String accountNumber, String accountName, String status, String freezeStatus) throws Exception {
        performCreateAccount(accountNumber, accountName, status, freezeStatus);
    }

    @When("I call account inquiry API for account number {string}")
    public void iCallAccountInquiryApiForAccountNumber(String accountNumber) throws Exception {
        lastResult = mockMvc.perform(get("/api/transactionManager/account/inquire/{accountNumber}", accountNumber))
                .andReturn();
    }

    @When("I call deposit API for account {string} with amount {string}")
    public void iCallDepositApiForAccountWithAmount(String accountNumber, String amount) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("accountNumber", accountNumber);
        body.put("amount", new BigDecimal(amount));
        body.put("crDrIndicator", "CREDIT");

        lastResult = mockMvc.perform(post("/api/transactionManager/credit")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();
    }

    @When("I call withdraw API for account {string} with amount {string}")
    public void iCallWithdrawApiForAccountWithAmount(String accountNumber, String amount) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("accountNumber", accountNumber);
        body.put("amount", new BigDecimal(amount));
        body.put("crDrIndicator", "DEBIT");

        lastResult = mockMvc.perform(post("/api/transactionManager/debit")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();
    }

    @When("I call transfer API from {string} to {string} with amount {string}")
    public void iCallTransferApiFromToWithAmount(String sourceAccount, String destinationAccount, String amount) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("sourceAccountNumber", sourceAccount);
        body.put("destinationAccountNumber", destinationAccount);
        body.put("amount", new BigDecimal(amount));

        lastResult = mockMvc.perform(post("/api/transactionManager/fundTransfer")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();
    }

    @When("I call transaction history API for account number {string}")
    public void iCallTransactionHistoryApiForAccountNumber(String accountNumber) throws Exception {
        lastResult = mockMvc.perform(get("/api/transactionManager/retrieveTransaction/account/{accountNumber}", accountNumber))
                .andReturn();
    }

    @Then("the response status is {int}")
    public void theResponseStatusIs(int expectedStatus) {
        assertStatus(expectedStatus);
    }

    @Then("the response field {string} equals {string}")
    public void theResponseFieldEquals(String fieldName, String expectedValue) throws Exception {
        JsonNode body = responseBody();
        Assertions.assertTrue(body.has(fieldName), "Missing response field: " + fieldName);
        Assertions.assertEquals(expectedValue, body.get(fieldName).asText());
    }

    @Then("the response field for retrieve transaction {string} equals {string}")
    public void theResponseFieldForRetrieveTransactionEquals(String fieldName, String expectedValue) throws Exception {
        JsonNode body = responseBody();
        Assertions.assertTrue(body.get(0).has(fieldName), "Missing response field: " + fieldName);
        Assertions.assertEquals(expectedValue, body.get(0).get(fieldName).asText());
    }

    @Then("the response field {string} is not blank")
    public void theResponseFieldIsNotBlank(String fieldName) throws Exception {
        JsonNode body = responseBody();
        Assertions.assertTrue(body.has(fieldName), "Missing response field: " + fieldName);
        Assertions.assertFalse(body.get(fieldName).asText().isBlank(), "Field is blank: " + fieldName);
    }

    @Then("account {string} balance should be {string}")
    public void accountBalanceShouldBe(String accountNumber, String expectedBalance) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AssertionError("Account not found: " + accountNumber));
        BigDecimal expected = new BigDecimal(expectedBalance);
        Assertions.assertEquals(0, expected.compareTo(account.getBalance()),
                "Balance mismatch for account " + accountNumber);
    }

    private void performCreateAccount(String accountNumber, String accountName, String status, String freezeStatus) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("accountNumber", accountNumber);
        body.put("accountName", accountName);
        body.put("status", status);
        body.put("freezeStatus", freezeStatus);

        lastResult = mockMvc.perform(post("/api/transactionManager/account/create")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();
    }

    private void assertStatus(int expectedStatus) {
        Assertions.assertNotNull(lastResult, "No response captured");
        int actualStatus = lastResult.getResponse().getStatus();
        if (actualStatus != expectedStatus) {
            Exception resolvedException = lastResult.getResolvedException();
            String body;
            try {
                body = lastResult.getResponse().getContentAsString();
            } catch (Exception ex) {
                body = "<failed to read response body: " + ex.getMessage() + ">";
            }
            String details = resolvedException == null
                    ? "none"
                    : resolvedException.getClass().getName() + ": " + resolvedException.getMessage();
            Assertions.fail("Expected status " + expectedStatus + " but was " + actualStatus
                    + ". Resolved exception: " + details + ". Body: " + body);
        }
    }

    private JsonNode responseBody() throws Exception {
        Assertions.assertNotNull(lastResult, "No response captured");
        String body = lastResult.getResponse().getContentAsString();
        return objectMapper.readTree(body);
    }
}
