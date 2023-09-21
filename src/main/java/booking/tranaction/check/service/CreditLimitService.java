package booking.tranaction.check.service;

import booking.tranaction.check.dto.request.RequestCreditLimitDto;
import booking.tranaction.check.dto.request.TransactionDto;
import booking.tranaction.check.dto.response.ResponseCreditLimitDto;
import booking.tranaction.check.model.query.CreditLimitQuery;
import reactor.core.publisher.Mono;

public interface CreditLimitService {
    Mono<ResponseCreditLimitDto> save(RequestCreditLimitDto requestCreditLimitDto);
    Mono<CreditLimitQuery> calculateUpdatedCreditLimit(CreditLimitQuery creditLimit, TransactionDto transactionDto);
}
