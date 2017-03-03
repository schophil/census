package lb.census.api.userbase;

import java.util.ArrayList;
import java.util.List;

import lb.census.rest.subjects.users.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lb.census.dao.UserDao;
import lb.census.model.User;

/**
 *
 * @author phili
 * @since 21-Nov-2015
 */
@CrossOrigin(origins = "http://localhost:63342")
@RestController
@RequestMapping("/{subject}/users")
public class UserBaseController {

    @Autowired
    private UserDao userDao;

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void updateOrCreate(@RequestBody UserInfo user) {

    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<UserInfo> getAllUsers(@PathVariable String subject) {
        List<User> users = userDao.getUsers(subject);

        List<UserInfo> userData = new ArrayList<>(users.size());
        users.forEach(user -> userData.add(new UserInfo(user)));
        return userData;
    }
}
