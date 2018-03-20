package lb.census;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CensusAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CensusAppApplication.class, args);
    }
}
