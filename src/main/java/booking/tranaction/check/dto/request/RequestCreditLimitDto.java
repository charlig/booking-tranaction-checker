package booking.tranaction.check.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record RequestCreditLimitDto(
        @NotBlank(message = "Email Id cannot be blank")
        @Email(message = "Email Id must be valid")
        String emailId,
        @NotNull(message = "Credit Limit cannot be blank")
        @Min(value = 0, message = "Credit Limit should not be less than 0")
        Long creditLimit) {
}
