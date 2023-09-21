package booking.tranaction.check.util;

import booking.tranaction.check.dto.request.TransactionDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class ParsingUtil {
    static Logger log = LoggerFactory.getLogger(ParsingUtil.class);

    public static Mono<TransactionDto> parseToTransactionDto(String transactionString){
        return Mono.fromCallable(()->transactionString)
                .filter(TransactionValidationUtil::isValidTransactionString)
                .flatMap(validTransaction->{
                    String[] parts = validTransaction.split(",");
                    TransactionDto transaction = new TransactionDto(parts[0],parts[1],parts[2],Long.parseLong(parts[3]),parts[4]);
                        return Mono.just(transaction);
                }).onErrorResume(e->{
                    log.error("Error parsing cost for transaction: {}", transactionString, e);
                    return Mono.error(e);
                });


    }


}
