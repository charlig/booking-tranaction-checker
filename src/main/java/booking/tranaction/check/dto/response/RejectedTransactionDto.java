package booking.tranaction.check.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RejectedTransactionDto(
        @JsonProperty("First Name")
        String firstName,
        @JsonProperty("Last Name")
        String lastName,
        @JsonProperty("Email Id")
        String emailId,
        @JsonProperty("Transaction Number")
        String transactionId,
        @JsonIgnore
        Boolean isReject) {
}
