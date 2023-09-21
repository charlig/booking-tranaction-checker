package booking.tranaction.check.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
public record ResponseRejectTransactionDto(@JsonProperty("Rejected Transactions")
                                            List<RejectedTransactionDto> rejectedTransactions) {

}
