package lb.census.record.recorders;

import java.util.Date;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lb.census.dao.UserDao;
import lb.census.model.User;
import lb.census.record.log.LogRecord;

/**
 * @author psc
 */
@Component
public class UserRecorder implements Recorder {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRecorder.class);
    private final HashSet<String> userIds = new HashSet<>();
    @Autowired
    private UserDao userDao;

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void record(LogRecord logRecord, RecorderContext recorderContext) {
        if (!"-".equals(logRecord.getUserId())) {
            userIds.add(logRecord.getUserId());
        }
    }

    @Override
    public void store(Date date, RecorderContext recorderContext) {
        for (String userId : userIds) {
            User user = userDao.getUser(userId);
            if (user == null) {
                user = new User();
                user.setUserId(userId);
                user.setName(userId);
                user.setSubject(recorderContext.getSubject().getId());
                LOGGER.debug("Creating user {}", user);
                userDao.save(user);
            }
        }
    }

    @Override
    public void forget(RecorderContext recorderContext) {
        userIds.clear();
    }
}
