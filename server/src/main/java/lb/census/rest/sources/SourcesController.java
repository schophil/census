package lb.census.rest.sources;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import lb.census.model.SourceIp;
import lb.census.rest.subjects.stats.StatsPerDayController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import lb.census.dao.SourceIpDao;

/**
 * Created by philippe on 20/04/16.
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/rest/sources")
public class SourcesController {

    private static Logger LOGGER = LoggerFactory.getLogger(SourcesController.class);

    @Autowired
    private SourceIpDao sourceIpDao;

    @RequestMapping()
    public Collection<OneSource> searchSources(@RequestParam("days") int days, @RequestParam("query") String query) {
        return pack(sourceIpDao.searchSourceIPs(query, days));
    }

    private Collection<OneSource> pack(List<SourceIp> ips) {
        HashMap<String, OneSource> sources = new HashMap<>();
        ips.stream().forEach(sourceIp -> {
            OneSource source = sources.get(sourceIp.getUserId());
            if (source == null) {
                source = new OneSource();
                source.userid = sourceIp.getUserId();
                sources.put(sourceIp.getUserId(), source);
            }
            OneSourceIp ip = new OneSourceIp();
            ip.ip = sourceIp.getIp();
            ip.lastUsed = sourceIp.getLastUsed();
            source.ips.add(ip);
        });

        return sources.values();
    }
}
