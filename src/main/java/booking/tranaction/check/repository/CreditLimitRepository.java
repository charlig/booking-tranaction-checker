package booking.tranaction.check.repository;

import booking.tranaction.check.model.command.CreditLimitCommand;
import booking.tranaction.check.model.query.CreditLimitQuery;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Repository
public interface CreditLimitRepository extends ReactiveMongoRepository<CreditLimitCommand,String> {
    Mono<CreditLimitQuery> findByEmailId(String emailId);

    Flux<CreditLimitQuery> findByEmailIdIn(Set<String> uniqueEmailIds);
}
