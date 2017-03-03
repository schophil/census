package lb.census.rest.subjects.users;

import lb.census.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by philippeschottey on 01/03/2017.
 */
@RestController
@RequestMapping("/rest/subjects/{subject}")
public class UserController {

    @Autowired
    private UserDao userDao;

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<UserInfo>> getAllUsers(@PathVariable String subject) {
        List<UserInfo> users = userDao.getUsers(subject).stream().map(u -> new UserInfo(u)).collect(toList());
        return ResponseEntity.ok(users);
    }
}
