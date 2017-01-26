package lb.census.config;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "source")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogSource {

    @XmlAttribute(name = "url")
    private String url;
    @XmlAttribute(name = "datePattern")
    private String datePattern;

    public LogSource(String url, String datePattern) {
        this.url = url;
        this.datePattern = datePattern;
    }

    public LogSource() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDatePattern() {
        return datePattern;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    public String getConcreteUrl(Calendar calendar) {
        return getConcreteUrl(calendar.getTime());
    }

    public String getConcreteUrl(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(getDatePattern());
        String dateAsString = sdf.format(date);
        return getUrl().replaceAll("\\{d\\}", dateAsString);
    }
}
