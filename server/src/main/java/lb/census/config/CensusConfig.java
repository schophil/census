package lb.census.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by phili on 19/12/2015.
 */
@XmlRootElement(name = "census")
@XmlAccessorType(XmlAccessType.FIELD)
public class CensusConfig {

    @XmlAttribute(name = "acid")
    private boolean acid = true;
    @XmlAttribute(name = "retrievalHour")
    private int retrievalHour;
    @XmlAttribute(name = "autoRetrieve")
    private boolean autoRetrieve = false;
    @XmlAttribute(name = "transformer")
    private String transformer;
    @XmlElementWrapper(name = "subjects")
    @XmlElement(name = "subject")
    private List<LogSubject> subjects;
    @XmlAttribute(name = "startRetrieve")
    private boolean startRetrieve = false;

    public boolean isStartRetrieve() {
        return startRetrieve;
    }

    public void setStartRetrieve(boolean startRetrieve) {
        this.startRetrieve = startRetrieve;
    }

    public boolean isAutoRetrieve() {
        return autoRetrieve;
    }

    public void setAutoRetrieve(boolean autoRetrieve) {
        this.autoRetrieve = autoRetrieve;
    }

    public List<LogSubject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<LogSubject> subjects) {
        this.subjects = subjects;
    }

    public boolean isAcid() {
        return acid;
    }

    public void setAcid(boolean acid) {
        this.acid = acid;
    }

    public int getRetrievalHour() {
        return retrievalHour;
    }

    public void setRetrievalHour(int retrievalHour) {
        this.retrievalHour = retrievalHour;
    }

    public String getTransformer() {
        return transformer;
    }

    public void setTransformer(String transformer) {
        this.transformer = transformer;
    }

    public LogSubject getSubjectWith(String id) {
        for (LogSubject logSubject : subjects) {
            if (id.equals(logSubject.getId())) {
                return logSubject;
            }
        }
        return null;
    }
}
