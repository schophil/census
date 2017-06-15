package lb.census.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Created by philippe on 14/06/2017.
 */
public class GradualAverageCalculator implements AverageCalculator {

    private BigDecimal currentAvg = BigDecimal.ZERO;
    private BigDecimal currentTotal = BigDecimal.ZERO;
    private int scale;

    public GradualAverageCalculator(int scale) {
        this.scale = scale;
    }

    @Override
    public int getScale() {
        return 0;
    }

    @Override
    public void add(int value) {
        add(new BigDecimal(value));
    }

    @Override
    public void add(double value) {
        add(new BigDecimal(value));
    }

    @Override
    public void add(long value) {
        add(new BigDecimal(value));
    }

    private void add(BigDecimal newValue) {
        BigDecimal newTotal = currentTotal.add(BigDecimal.ONE);
        currentAvg = currentAvg.
                multiply(currentTotal.divide(newTotal, 10, RoundingMode.CEILING)).
                add(newValue.divide(newTotal, 10, RoundingMode.CEILING));
        currentTotal = newTotal;
    }

    @Override
    public void clear() {
        currentAvg = BigDecimal.ZERO;
        currentTotal = BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getCurrentAverage() {
        return currentAvg.setScale(scale, RoundingMode.HALF_UP);
    }
}
