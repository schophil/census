package lb.census.api.report;

import lb.census.api.CensusController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author philippe
 * @since 19/10/2016
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/report")
public class UserReportController extends CensusController {

    @RequestMapping("/requests/{subject}/{from}/{to}/{user}")
    public ReportData queryRequests(@PathVariable String subject, @PathVariable String from, @PathVariable String to,
                                    @PathVariable String user) {

        

        return new ReportData();
    }
}
