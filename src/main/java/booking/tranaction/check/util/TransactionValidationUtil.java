package booking.tranaction.check.util;

import booking.tranaction.check.dto.request.TransactionDto;
import booking.tranaction.check.model.query.CreditLimitQuery;
import reactor.core.publisher.Mono;

public class TransactionValidationUtil {
    public static boolean isValidTransactionString(String transactionString) {
        // Basic validation: check if the string has 5 comma separated parts
        return transactionString.split(",").length == 5;
    }

    public static Mono<CreditLimitQuery> hasNotSufficientFunds(TransactionDto transactionDto, CreditLimitQuery creditLimit) {
        return Mono.fromSupplier(()->CreditLimitQuery.builder()
                .isReject(transactionDto.cost() > creditLimit.getCreditLimit())
                .emailId(creditLimit.getEmailId())
                .creditLimit(creditLimit.getCreditLimit())
                .id(creditLimit.getId())
                .build());
    }

}
