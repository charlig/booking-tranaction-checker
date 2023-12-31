package booking.tranaction.check;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class BookingTransactionCheckerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingTransactionCheckerApplication.class, args);
	}

}
