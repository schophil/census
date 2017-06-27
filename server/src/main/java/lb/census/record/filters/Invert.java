package lb.census.record.filters;

import lb.census.record.log.LogRecord;

import javax.xml.bind.annotation.*;

/**
 * Created by philippe on 27/06/2017.
 */
@XmlRootElement(name = "invert-filter")
@XmlAccessorType(XmlAccessType.FIELD)
public class Invert extends Filter {

    @XmlElementRef
    private Filter wrapped;

    public Invert(Filter wrapped) {
        this.wrapped = wrapped;
    }

    public Invert() {
    }

    public Filter getWrapped() {
        return wrapped;
    }

    public void setWrapped(Filter wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public boolean passes(String line) {
        return !wrapped.passes(line);
    }

    @Override
    public boolean passes(LogRecord logRecord) {
        return !wrapped.passes(logRecord);
    }
}
