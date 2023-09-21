package booking.tranaction.check.controller;

import booking.tranaction.check.config.TestConfig;
import booking.tranaction.check.dto.response.RejectedTransactionDto;
import booking.tranaction.check.dto.response.ResponseRejectTransactionDto;
import booking.tranaction.check.excepton.CustomNullPointerException;
import booking.tranaction.check.excepton.UnsavedCreditLimitException;
import booking.tranaction.check.model.query.CreditLimitQuery;
import booking.tranaction.check.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;


@WebFluxTest(controllers = TransactionController.class)
@Import(TestConfig.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
class TransactionControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    private static final String URL = "/api/v1/transaction";

    @BeforeEach
    public void setup() {
        // Clean database before each test
        reactiveMongoTemplate.dropCollection(CreditLimitQuery.class).block();
    }

    @Test
    public void testGetRejectedTransactions() {
        // Given
        List<String> transactions = List.of("transaction1", "transaction2");
        ResponseRejectTransactionDto response = new ResponseRejectTransactionDto(
                List.of(new RejectedTransactionDto("John","Doe","john.doe™gmail.com", "TR001", true))
        ); // your expected response

        when(transactionService.getRejectTransactions(anyList())).thenReturn(Mono.just(response));

        // When & Then
        webTestClient.post()
                .uri(URL + "/rejected")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(transactions))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseRejectTransactionDto.class);

    }
    @Test
    public void testEmailNotFound() {
        // Given
        List<String> transactions = List.of("transaction1");
        when(transactionService.getRejectTransactions(anyList())).thenReturn(Mono.error(new ChangeSetPersister.NotFoundException()));

        // When & Then
        webTestClient.post()
                .uri(URL + "/rejected")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(transactions))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testNullPointerException() {
        // Given
        List<String> transactions = List.of("transaction1");
        when(transactionService.getRejectTransactions(anyList())).thenReturn(Mono.error(new CustomNullPointerException("A null value was encountered!")));

        // When & Then
        webTestClient.post()
                .uri(URL + "/rejected")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(transactions))
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    public void testFailedToSaveCreditLimit() {
        // Given
        List<String> transactions = List.of("transaction1");
        when(transactionService.getRejectTransactions(anyList())).thenReturn(Mono.error(new UnsavedCreditLimitException("Failed to save credit limit")));

        // When & Then
        webTestClient.post()
                .uri(URL + "/rejected")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(transactions))
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    public void testInvalidRequestBody() {
        // Given no body

        // When & Then
        webTestClient.post()
                .uri(URL + "/rejected")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

}