package lb.census.config;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by phili on 22/12/2015.
 */
@Component
public class Resources {

    public static final String NO_DATA = "NO_DATA";

    @Value("${census.data:NO_DATA}")
    private String data;
    @Value("${census.config:NO_DATA}")
    private String config;

    public Resources() {
    }

    public Resources(String data, String config) {
        this.data = data;
        this.config = config;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public URL getCensusConfig() {
        if (NO_DATA.equals(config)) {
            return null;
        }
        return getUrlFor(config);
    }

    private URL getUrlFor(String resource) {
        if (NO_DATA.equals(resource)) {
            return null;
        }
        try {
            return new URL(data + resource);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
