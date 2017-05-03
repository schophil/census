package lb.census.api.ping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lb.census.record.RecordService;

/**
 * @author phili
 * @since 21-Nov-2015
 */
public class PingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PingController.class);

    private RecordService recordService;

    public String ping() {
        LOGGER.info("Using record service {}", recordService);
        return "pong";
    }
}
