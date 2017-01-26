package lb.census.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lb.census.model.User;

/**
 * Created by philippe on 30/12/15.
 */
public class UserBaseReducer {

    private Map<String, User> userBaseMap;

    public UserBaseReducer(Collection<User> userBase) {
        userBaseMap = new HashMap<>();
        for (User user : userBase) {
            userBaseMap.put(user.getUserId(), user);
        }
    }

    public User reduce(String userId) {
        return userBaseMap.remove(userId);
    }

    public Collection<User> remaining() {
        return userBaseMap.values();
    }
}
