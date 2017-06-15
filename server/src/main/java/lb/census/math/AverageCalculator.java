package lb.census.math;

import java.math.BigDecimal;

/**
 * Created by philippe on 14/06/2017.
 */
public interface AverageCalculator {

    static AverageCalculator create(int scale) {
        return new GradualAverageCalculator(scale);
    }

    int getScale();

    void add(int value);

    void add(double value);

    void add(long value);

    void clear();

    BigDecimal getCurrentAverage();
}
