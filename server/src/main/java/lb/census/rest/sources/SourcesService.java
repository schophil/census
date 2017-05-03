package lb.census.rest.sources;

import lb.census.dao.SourceIpDao;
import lb.census.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by philippe on 27/04/2017.
 */
public class SourcesService {

    @Autowired
    private SourceIpDao sourceIpDao;

    @Autowired
    private UserDao userDao;


}
