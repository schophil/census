package lb.census.math;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by philippe on 14/06/2017.
 */
@RunWith(SpringRunner.class)
public class AverageCalculatorTest {

    @Test
    public void average1() {
        AverageCalculator averageCalculator = new GradualAverageCalculator(2);

        averageCalculator.add(3);
        averageCalculator.add(5);
        averageCalculator.add(2);
        averageCalculator.add(7);
        averageCalculator.add(4);

        BigDecimal avg = averageCalculator.getCurrentAverage();

        assertThat(avg.doubleValue(), is(4.2));
    }

    @Test
    public void average2() {
        AverageCalculator averageCalculator = new GradualAverageCalculator(2);

        averageCalculator.add(3);
        averageCalculator.add(5);
        averageCalculator.add(2);
        averageCalculator.add(7);
        averageCalculator.add(4);
        averageCalculator.add(9);
        averageCalculator.add(11);
        averageCalculator.add(15);
        averageCalculator.add(1);
        averageCalculator.add(3);
        averageCalculator.add(8);

        BigDecimal avg = averageCalculator.getCurrentAverage();

        assertThat(avg.doubleValue(), is(6.18));
    }
}
