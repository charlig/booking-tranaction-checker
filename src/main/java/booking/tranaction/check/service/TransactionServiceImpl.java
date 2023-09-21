package booking.tranaction.check.service;

import booking.tranaction.check.dto.request.TransactionDto;
import booking.tranaction.check.dto.response.RejectedTransactionDto;
import booking.tranaction.check.dto.response.ResponseRejectTransactionDto;
import booking.tranaction.check.excepton.CustomNullPointerException;
import booking.tranaction.check.excepton.UnsavedCreditLimitException;
import booking.tranaction.check.model.query.CreditLimitQuery;
import booking.tranaction.check.repository.CreditLimitRepository;
import booking.tranaction.check.util.ParsingUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService{
    private final CreditLimitRepository creditLimitQueryRepository;
    private final CreditLimitService creditLimitService;
    Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Override
    public Mono<ResponseRejectTransactionDto> getRejectTransactions(List<String> transactions) {
        return Flux.fromIterable(transactions)
                .concatMap(ParsingUtil::parseToTransactionDto)
                .concatMap(this::validateAndProcessTransaction)
                .filter(RejectedTransactionDto::isReject)
                .collectList()
                .map(ResponseRejectTransactionDto::new);
    }


    @Override
    public Mono<RejectedTransactionDto> validateAndProcessTransaction(TransactionDto transactionDto) {
        Map<String, CreditLimitQuery> map = new ConcurrentHashMap<>();
        return Mono.defer(() -> {
                    // Check if the map contains the emailId
                    if (map.containsKey(transactionDto.emailId())) {
                        return Mono.justOrEmpty(map.get(transactionDto.emailId()));
                    } else {
                        return creditLimitQueryRepository.findByEmailId(transactionDto.emailId())
                                .doOnNext(creditLimitQuery -> map.put(transactionDto.emailId(), creditLimitQuery))
                                .switchIfEmpty(Mono.error(new ChangeSetPersister.NotFoundException()));
                    }

                })
                .flatMap(creditLimitQuery -> creditLimitService.calculateUpdatedCreditLimit(creditLimitQuery, transactionDto))
                .onErrorMap(e -> {
                    if (e instanceof NullPointerException) {
                        return new CustomNullPointerException("A null value was encountered!", e);
                    } else if (e instanceof ChangeSetPersister.NotFoundException) {
                        return new ChangeSetPersister.NotFoundException();
                    }
                    return new UnsavedCreditLimitException("Failed to save credit limit for " + transactionDto.emailId(), e);
                })
                .map(creditLimitQuery -> new RejectedTransactionDto(
                        transactionDto.firstName(),
                        transactionDto.lastName(),
                        transactionDto.emailId(),
                        transactionDto.transactionId(),
                        creditLimitQuery.getIsReject()));
    }
}
