package lb.census;

import lb.census.config.Resources;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

/**
 * Created by philippeschottey on 14/02/2017.
 */
@Configuration
@EnableAutoConfiguration(exclude = {
        WebMvcAutoConfiguration.class
})
@ComponentScan
public class TestScope {

    public static void main(String[] args) {
        SpringApplication.run(TestScope.class, args);
    }
}
