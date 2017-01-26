package lb.census.api.sources;

import java.util.Collection;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lb.census.dao.SourceIpDao;

/**
 * Created by philippe on 20/04/16.
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/{subject}/sources")
public class SourcesController {

    @Autowired
    private SourceIpDao sourceIpDao;

    @RequestMapping("/last/{days}")
    public Collection<OneSource> getAllSources(@PathVariable("subject") String subject, @PathVariable("days") int days) {
        HashMap<String, OneSource> sources = new HashMap<>();
        sourceIpDao.getSourceIPsFor(days, subject).stream().forEach(sourceIp -> {
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
