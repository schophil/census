package lb.census.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class AverageCalculator {

    private final List<BigDecimal> averages = new ArrayList<>();
    private BigDecimal currentSum = BigDecimal.ZERO;
    private int currentTotal = 0;
    private int groupSize = 1000;
    private int scale = 2;

    public AverageCalculator() {
    }

    public AverageCalculator(int groupSize, int scale) {
        this.groupSize = groupSize;
        this.scale = scale;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public int getScale() {
        return scale;
    }

    public void add(int value) {
        update(new BigDecimal(value));
    }

    public void add(double value) {
        update(new BigDecimal(value));
    }

    public void add(long value) {
        update(new BigDecimal(value));
    }

    public void clear() {
        averages.clear();
        currentTotal = 0;
        currentSum = BigDecimal.ZERO;
    }

    public BigDecimal getCurrentAverage() {
        if (averages.isEmpty() && currentTotal == 0) {
            return BigDecimal.ZERO;
        }

        if (averages.isEmpty() && currentTotal != 0) {
            return calculateCurrentAverage();
        }

        BigDecimal sumOfAverages = BigDecimal.ZERO;
        for (BigDecimal average : averages) {
            sumOfAverages = sumOfAverages.add(average);
        }

        if (currentTotal != 0) {
            BigDecimal currentAverage = calculateCurrentAverage();
            sumOfAverages = sumOfAverages.add(currentAverage);
            BigDecimal total = new BigDecimal(averages.size() + 1);
            return sumOfAverages.divide(total, 2, RoundingMode.HALF_DOWN);
        } else {
            return sumOfAverages.divide(new BigDecimal(averages.size()), 2, RoundingMode.HALF_DOWN);
        }
    }

    private void update(BigDecimal value) {
        currentTotal++;
        currentSum = currentSum.add(value);
        if (currentTotal == groupSize) {
            BigDecimal average = calculateCurrentAverage();
            averages.add(average);
            currentTotal = 0;
            currentSum = BigDecimal.ZERO;
        }
    }

    private BigDecimal calculateCurrentAverage() {
        if (currentTotal == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal average = currentSum.divide(new BigDecimal(currentTotal), 2,
                RoundingMode.HALF_DOWN);
        return average;
    }
}
