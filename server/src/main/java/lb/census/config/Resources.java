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

    @Value("${census.data}")
    private String data;
    @Value("${census.config}")
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
        return getUrlFor(config);
    }

    private URL getUrlFor(String resource) {
        try {
            return new URL(data + resource);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
