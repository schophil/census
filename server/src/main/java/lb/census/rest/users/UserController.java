package lb.census.rest.users;

import lb.census.dao.UserDao;
import lb.census.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by philippeschottey on 01/03/2017.
 */
@RestController
@RequestMapping("/rest/users")
public class UserController {

    @Autowired
    private UserDao userDao;

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<UserInfo>> getAllUsers(@RequestParam String subject) {
        if (StringUtils.isEmpty(subject)) {
            subject = "ALL";
        }
        List<UserInfo> users = userDao.getUsers(subject).stream().map(u -> new UserInfo(u)).collect(toList());
        return ResponseEntity.ok(users);
    }

    @RequestMapping(value = "/{id}")
    public ResponseEntity<UserInfo> get(@PathVariable String id) {
        User user = userDao.getUser(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UserInfo(user));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<UserInfo> add(@PathVariable String id) {
        User user = userDao.getUser(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UserInfo(user));
    }
}
