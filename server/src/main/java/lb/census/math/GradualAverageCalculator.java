package lb.census.math;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by philippe on 14/06/2017.
 */
public class GradualAverageCalculator implements AverageCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger("CENSUS_MATH");
    private double currentAvg = 0;
    private double currentTotal = 0;
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
        addDouble(value);
    }

    @Override
    public void add(double value) {
        addDouble(value);
    }

    @Override
    public void add(long value) {
        addDouble(value);
    }

    private void addDouble(double newValue) {
        double newTotal = currentTotal + 1;
        currentAvg = (currentAvg * (currentTotal / newTotal)) + (newValue / newTotal);
        currentTotal = newTotal;
        LOGGER.trace("After adding value {}: {}", newValue, this);
    }

    @Override
    public void clear() {
        currentAvg = 0;
        currentTotal = 0;
    }

    @Override
    public BigDecimal getCurrentAverage() {
        return new BigDecimal(currentAvg).setScale(scale, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "GradualAverageCalculator{" +
                "currentAvg=" + currentAvg +
                ", currentTotal=" + currentTotal +
                ", scale=" + scale +
                '}';
    }
}
