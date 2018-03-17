package lb.census.record.filters;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by philippe on 28/06/2017.
 */
public class PatternFilterTest {

    @Test
    public void simple() {
        PatternFilter patternFilter = new PatternFilter();
        patternFilter.setRegexp(".*/myserver/.*");

        Assert.assertFalse(patternFilter.passes("GET /google."));
        Assert.assertTrue(patternFilter.passes("GET /myserver/home.html"));
    }

    @Test
    public void negation() {
        PatternFilter patternFilter = new PatternFilter();
        patternFilter.setRegexp(".*darthvader.*");

        Assert.assertFalse(patternFilter.invert().passes("Luke I am darthvader, your father."));
        Assert.assertTrue(patternFilter.invert().passes("Luke I am your father"));
    }
}
