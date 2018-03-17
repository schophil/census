package lb.census.config;

import lb.census.record.filters.Invert;
import lb.census.record.filters.PatternFilter;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by philippe on 27/06/2017.
 */
public class CensusconfigTest {

    @Test
    public void readConfig() {
        CensusConfigFactory factory = new CensusConfigFactory();
        CensusConfig censusConfig = factory.loadFrom(getClass().getResource("/config.xml"));

        assertThat(censusConfig).isNotNull();
        assertThat(censusConfig.getSubjects()).isNotNull();
        assertThat(censusConfig.getSubjects()).isNotEmpty();
        assertThat(censusConfig.getSubjects().size()).isOne();

        assertThat(censusConfig.getSubjects().get(0)).isNotNull();
        assertThat(censusConfig.getSubjects().get(0).getId()).isEqualTo("1");
        assertThat(censusConfig.getSubjects().get(0).getName()).isEqualTo("a");

        assertThat(censusConfig.getSubjects().get(0).getFilters()).isNotNull();
        assertThat(censusConfig.getSubjects().get(0).getFilters()).isNotEmpty();
        assertThat(censusConfig.getSubjects().get(0).getFilters().size()).isOne();

        assertThat(censusConfig.getSubjects().get(0).getFilters().get(0)).isInstanceOf(Invert.class);

        Invert invert = (Invert) censusConfig.getSubjects().get(0).getFilters().get(0);
        assertThat(invert.getWrapped()).isNotNull();
        assertThat(invert.getWrapped()).isInstanceOf(PatternFilter.class);
    }
}
