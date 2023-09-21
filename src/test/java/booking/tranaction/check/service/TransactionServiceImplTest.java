package booking.tranaction.check.service;

import booking.tranaction.check.dto.request.TransactionDto;
import booking.tranaction.check.dto.response.RejectedTransactionDto;
import booking.tranaction.check.dto.response.ResponseRejectTransactionDto;
import booking.tranaction.check.excepton.CustomNullPointerException;
import booking.tranaction.check.excepton.UnsavedCreditLimitException;
import booking.tranaction.check.model.query.CreditLimitQuery;
import booking.tranaction.check.repository.CreditLimitRepository;
import booking.tranaction.check.util.ParsingUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.crossstore.ChangeSetPersister;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private CreditLimitRepository creditLimitQueryRepository;

    @Mock
    private CreditLimitService creditLimitService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHappyPath() {
        // Sample data
        TransactionDto transactionDto = new TransactionDto("John", "Doe", "john.doe@example.com", 1000L, "1000");
        CreditLimitQuery initialCreditLimitQuery = new CreditLimitQuery("1", "john.doe@example.com", 500L, false);
        CreditLimitQuery updatedCreditLimitQuery = new CreditLimitQuery("2", "john.doe@example.com", 400L, false); // After deducting 1000

        // Mock behavior
        try (MockedStatic<ParsingUtil> mocked = mockStatic(ParsingUtil.class)) {
            mocked.when(() -> ParsingUtil.parseToTransactionDto(anyString())).thenReturn(Mono.just(transactionDto));
            when(creditLimitQueryRepository.findByEmailId(anyString())).thenReturn(Mono.just(initialCreditLimitQuery));
            when(creditLimitService.calculateUpdatedCreditLimit(any(CreditLimitQuery.class), any(TransactionDto.class))).thenReturn(Mono.just(updatedCreditLimitQuery));

            // Call the method
            Mono<ResponseRejectTransactionDto> result = transactionService.getRejectTransactions(List.of("transaction1"));

            // Verify the results using StepVerifier
            StepVerifier.create(result)
                    .assertNext(response -> {
                        List<RejectedTransactionDto> rejectedTransactions = response.rejectedTransactions();
                        assertEquals(0, rejectedTransactions.size()); // Expecting no rejected transactions
                    })
                    .verifyComplete();

            // Verify interactions with mocks
            verify(creditLimitQueryRepository).findByEmailId(anyString());
            verify(creditLimitService).calculateUpdatedCreditLimit(any(CreditLimitQuery.class), any(TransactionDto.class));

        }

    }

    @Test
    void testEmailNotFound() {
        // Sample data
        TransactionDto transactionDto = new TransactionDto("John", "Doe", "john.doe@example.com", 1000L, "1000");

        // Mock behavior
        try (MockedStatic<ParsingUtil> mocked = mockStatic(ParsingUtil.class)) {
            mocked.when(() -> ParsingUtil.parseToTransactionDto(anyString())).thenReturn(Mono.just(transactionDto));
            when(creditLimitQueryRepository.findByEmailId(anyString())).thenReturn(Mono.empty());

            // Call the method
            Mono<ResponseRejectTransactionDto> result = transactionService.getRejectTransactions(List.of("transaction1"));

            // Verify the results using StepVerifier
            StepVerifier.create(result)
                    .expectError(ChangeSetPersister.NotFoundException.class)
                    .verify();
        }
    }

    @Test
    void testNullPointerException() {

        // Sample data
        TransactionDto transactionDto = new TransactionDto("John", "Doe", "john.doe@example.com", 12345L, "1000");
        CreditLimitQuery creditLimitQuery = new CreditLimitQuery(null, "john.doe@example.com", 500L,false);

        try (MockedStatic<ParsingUtil> mocked = mockStatic(ParsingUtil.class)) {
            mocked.when(() -> ParsingUtil.parseToTransactionDto(anyString())).thenReturn(Mono.just(transactionDto));
            // Mock behavior
            when(ParsingUtil.parseToTransactionDto(anyString())).thenReturn(Mono.just(transactionDto));
            when(creditLimitQueryRepository.findByEmailId(anyString())).thenReturn(Mono.just(creditLimitQuery));
            when(creditLimitService.calculateUpdatedCreditLimit(any(), any())).thenThrow(new NullPointerException());

            // Call the method
            Mono<ResponseRejectTransactionDto> result = transactionService.getRejectTransactions(List.of("transaction1"));

            // Verify the results using StepVerifier
            StepVerifier.create(result)
                    .expectError(CustomNullPointerException.class)
                    .verify();
        }
    }

    @Test
    void testUnsavedCreditLimitException() {
        // Sample data
        TransactionDto transactionDto = new TransactionDto("John", "Doe", "john.doe@example.com", 12345L, "1000");
        CreditLimitQuery creditLimitQuery = new CreditLimitQuery(null, "john.doe@example.com", 500L,false);

        try (MockedStatic<ParsingUtil> mocked = mockStatic(ParsingUtil.class)) {
            mocked.when(() -> ParsingUtil.parseToTransactionDto(anyString())).thenReturn(Mono.just(transactionDto));
            // Mock behavior
            when(ParsingUtil.parseToTransactionDto(anyString())).thenReturn(Mono.just(transactionDto));
            when(creditLimitQueryRepository.findByEmailId(anyString())).thenReturn(Mono.just(creditLimitQuery));
            when(creditLimitService.calculateUpdatedCreditLimit(any(), any())).thenThrow(new RuntimeException());

            // Call the method
            Mono<ResponseRejectTransactionDto> result = transactionService.getRejectTransactions(List.of("transaction1"));

            // Verify the results using StepVerifier
            StepVerifier.create(result)
                    .expectError(UnsavedCreditLimitException.class)
                    .verify();
        }
    }
}