package lb.census.utils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generators unique ids to be used in the database.
 *
 * @author phili
 */
public class TemporalIdGenerator implements IdentifierGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemporalIdGenerator.class);
    private static final int MAX_SIZE = 36;

    public String generate() {
        StringBuilder id = new StringBuilder();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Calendar c = Calendar.getInstance();
        id.append(sdf.format(c.getTime()));

        // end with random bits
        id.append(RandomStringUtils.randomAlphabetic(MAX_SIZE - id.length()));

        LOGGER.trace("Generated id {}", id);
        return id.toString();
    }

    @Override
    public Serializable generate(SessionImplementor arg0, Object arg1) throws HibernateException {
        return generate();
    }
}
