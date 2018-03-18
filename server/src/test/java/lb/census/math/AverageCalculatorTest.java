package lb.census.math;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by philippe on 14/06/2017.
 */
@RunWith(SpringRunner.class)
public class AverageCalculatorTest {

    @Test
    public void averageGradualIntegersOne() {
        AverageCalculator averageCalculator = new GradualAverageCalculator(2);

        averageCalculator.add(3);
        averageCalculator.add(5);
        averageCalculator.add(2);
        averageCalculator.add(7);
        averageCalculator.add(4);

        BigDecimal avg = averageCalculator.getCurrentAverage();

        assertThat(avg.doubleValue()).isEqualTo(4.2);
    }

    @Test
    public void averageGradualIntegersTwo() {
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

        assertThat(avg.doubleValue()).isEqualTo(6.18);
    }

//    @Test
//    public void averageGradualDoublesOne() {
//        AverageCalculator averageCalculator = new GradualAverageCalculator(2);
//
//        averageCalculator.add(0.02);
//        averageCalculator.add(0.02);
//        averageCalculator.add(0.04);
//        averageCalculator.add(0.06);
//
//        BigDecimal avg = averageCalculator.getCurrentAverage();
//
//        assertThat(avg.doubleValue()).isEqualTo(0.04);
//    }


    @Test
    public void averageSimple1() {
        AverageCalculator averageCalculator = new SimpleAverageCalculator(2);

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

        assertThat(avg.doubleValue()).isEqualTo(6.18);
    }

    @Test
    public void averageSimple2() {
        AverageCalculator averageCalculator = new SimpleAverageCalculator(2);

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

        assertThat(avg.doubleValue()).isEqualTo(6.18);
    }
}
