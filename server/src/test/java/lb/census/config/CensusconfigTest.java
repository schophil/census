package lb.census.config;

import lb.census.record.filters.Filter;
import lb.census.record.filters.Invert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by philippe on 27/06/2017.
 */
@RunWith(SpringRunner.class)
public class CensusconfigTest {

    @Test
    public void readConfig() {
        CensusConfigFactory factory = new CensusConfigFactory();
        CensusConfig censusConfig = factory.loadFrom(getClass().getResource("/config.xml"));

        Assert.assertNotNull(censusConfig);
        Assert.assertNotNull(censusConfig.getSubjects());

        Assert.assertFalse(censusConfig.getSubjects().isEmpty());
        Assert.assertFalse(censusConfig.getSubjects().get(0).getFilters().isEmpty());

        Filter filter = censusConfig.getSubjects().get(0).getFilters().get(0);
        Assert.assertTrue(filter instanceof Invert);

        Invert invert = (Invert) filter;
        Assert.assertNotNull(invert.getWrapped());
    }
}
