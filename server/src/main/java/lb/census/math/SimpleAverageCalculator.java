package lb.census.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by philippe on 21/06/2017.
 */
public class SimpleAverageCalculator implements AverageCalculator {

    private float sum = 0;
    private long total = 0;
    private int scale;

    public SimpleAverageCalculator(int scale) {
        this.scale = scale;
    }

    @Override
    public int getScale() {
        return 0;
    }

    @Override
    public void add(int value) {
        sum += value;
        total++;
    }

    @Override
    public void add(double value) {
        sum += value;
        total++;
    }

    @Override
    public void add(long value) {
        sum += value;
        total++;
    }

    @Override
    public void clear() {
        sum = 0;
        total = 0;
    }

    @Override
    public BigDecimal getCurrentAverage() {
        return new BigDecimal(sum).divide(new BigDecimal(total), scale, RoundingMode.HALF_UP);
    }
}
