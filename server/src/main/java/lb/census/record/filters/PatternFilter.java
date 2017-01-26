package lb.census.record.filters;

import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lb.census.record.log.LogRecord;

/**
 * Filters log entries based on a regular expression.
 */
@XmlRootElement(name = "pattern-filter")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatternFilter extends Filter {

    @XmlAttribute
    private String regexp;
    private Pattern pattern;

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public Pattern getPattern() {
        if (pattern == null) {
            pattern = Pattern.compile(regexp);
        }
        return pattern;
    }

    @Override
    public boolean passes(String line) {
        Pattern pattern = getPattern();
        if (pattern == null) {
            return true;
        }
        return !pattern.matcher(line).matches();
    }

    @Override
    public boolean passes(LogRecord logRecord) {
        return !pattern.matcher(logRecord.getOriginalLine()).matches();
    }

    @Override
    public String toString() {
        return "PatternFilter{" + "regexp=" + regexp + '}';
    }
}
