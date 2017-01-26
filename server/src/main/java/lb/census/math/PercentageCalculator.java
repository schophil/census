package lb.census.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PercentageCalculator {

    public double forPart(BigDecimal base, BigDecimal part) {
        BigDecimal deviation = part.multiply(new BigDecimal(100)).divide(base,
                2, RoundingMode.HALF_DOWN);
        return deviation.doubleValue();
    }

    public double forDifference(BigDecimal base, BigDecimal newValue) {
        BigDecimal difference = base.subtract(newValue).abs();
        return forPart(base, difference);
    }
}
