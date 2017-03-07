package lb.census.dao;

import java.util.Date;
import java.util.List;

import lb.census.model.User;

public interface UserDao {

    User getUser(String userId);

    List<User> getUsers(String subject);

    List<User> getUsersFor(Date date, String subject);

    List<String> getCategories();

    User save(User user);
}
