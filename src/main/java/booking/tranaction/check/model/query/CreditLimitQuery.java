package booking.tranaction.check.model.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import reactor.core.publisher.Mono;

@Data
@Builder
@AllArgsConstructor
public class CreditLimitQuery  {
    private String id;
    private String emailId;
    private Long creditLimit;
    private Boolean isReject;
}
