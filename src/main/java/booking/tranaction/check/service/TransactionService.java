package booking.tranaction.check.service;

import booking.tranaction.check.dto.request.TransactionDto;
import booking.tranaction.check.dto.response.RejectedTransactionDto;
import booking.tranaction.check.dto.response.ResponseRejectTransactionDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TransactionService {
    Mono<ResponseRejectTransactionDto> getRejectTransactions(List<String> transactions);
    Mono<RejectedTransactionDto> validateAndProcessTransaction(TransactionDto transactionDto);
}
