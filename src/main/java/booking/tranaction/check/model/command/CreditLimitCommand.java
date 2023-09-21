package booking.tranaction.check.model.command;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document
@TypeAlias("CreditLimit")
@Builder
@Data
public class CreditLimitCommand {
    @Id
    private String id;
    @NotNull
    @Indexed(unique = true)
    private String emailId;
    @NonNull
    private Long creditLimit;
}
