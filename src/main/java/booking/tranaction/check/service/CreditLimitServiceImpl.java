package booking.tranaction.check.service;

import booking.tranaction.check.dto.request.RequestCreditLimitDto;
import booking.tranaction.check.dto.request.TransactionDto;
import booking.tranaction.check.dto.response.ResponseCreditLimitDto;
import booking.tranaction.check.model.command.CreditLimitCommand;
import booking.tranaction.check.model.query.CreditLimitQuery;
import booking.tranaction.check.repository.CreditLimitRepository;
import booking.tranaction.check.util.ParsingUtil;
import com.mongodb.DuplicateKeyException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreditLimitServiceImpl implements CreditLimitService{
    private final CreditLimitRepository creditLimitRepository;
     Logger log = LoggerFactory.getLogger(CreditLimitServiceImpl.class);
    @Override
    public Mono<ResponseCreditLimitDto> save(RequestCreditLimitDto requestCreditLimitDto) {
        return creditLimitRepository.findByEmailId(requestCreditLimitDto.emailId())
                .flatMap(creditLimitQuery -> {
                    CreditLimitCommand command = CreditLimitCommand
                            .builder()
                            .id(creditLimitQuery.getId())
                            .creditLimit(creditLimitQuery.getCreditLimit() + requestCreditLimitDto.creditLimit())
                            .emailId(requestCreditLimitDto.emailId()).build();
                    return creditLimitRepository.save(command);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    CreditLimitCommand command = CreditLimitCommand
                            .builder()
                            .creditLimit(requestCreditLimitDto.creditLimit())
                            .emailId(requestCreditLimitDto.emailId()).build();
                    return creditLimitRepository.save(command);
                }))
                .map(saveCreditLimit -> new ResponseCreditLimitDto(saveCreditLimit.getId(),
                        saveCreditLimit.getEmailId(),
                        saveCreditLimit.getCreditLimit()))
                .onErrorResume(DuplicateKeyException.class, e -> {
                    // Handle the duplicate key exception here, e.g., log it and return a meaningful error response.
                    return Mono.error(new RuntimeException("Email already exists!"));
                });
    }

    public Mono<CreditLimitQuery> calculateUpdatedCreditLimit(CreditLimitQuery creditLimit, TransactionDto transactionDto) {
        if (transactionDto.cost() <= creditLimit.getCreditLimit()) {
            long newLimit = creditLimit.getCreditLimit() - transactionDto.cost();
            CreditLimitCommand creditLimitCommand = CreditLimitCommand.builder()
                    .creditLimit(newLimit)
                    .emailId(creditLimit.getEmailId())
                    .id(creditLimit.getId())
                    .build();

            return creditLimitRepository.save(creditLimitCommand)
                    .onErrorResume(e -> {
                        log.error("Error saving credit limit", e);
                        return Mono.error(e);
                    })
                    .then(Mono.just(CreditLimitQuery.builder()
                            .isReject(false)
                            .emailId(creditLimit.getEmailId())
                            .creditLimit(newLimit)
                            .id(creditLimit.getId())
                            .build()));
        }

        return Mono.just(CreditLimitQuery.builder()
                .isReject(true)
                .emailId(creditLimit.getEmailId())
                .creditLimit(creditLimit.getCreditLimit())
                .id(creditLimit.getId())
                .build());
    }
}
