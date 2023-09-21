package booking.tranaction.check.controller;

import booking.tranaction.check.dto.response.ResponseRejectTransactionDto;
import booking.tranaction.check.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping(value = "/rejected",consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseRejectTransactionDto> getRejectedTransactions(
            @RequestBody @NotBlank(message = "Request body cannot be blank")
            List<String> transactions){
        return transactionService.getRejectTransactions(transactions);

    }
}
