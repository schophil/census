package lb.census.rest.subjects.stats;

import lb.census.model.Resource;
import lb.census.rest.shared.Metrics;

/**
 * Created by philippe on 26/04/16.
 */
public class OneResource extends Metrics {

    public String path;

    public static OneResource of(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource is null.");
        }
        OneResource oneResource = new OneResource();
        oneResource.map(resource);
        oneResource.path = resource.getTextValue();
        return oneResource;
    }
}
