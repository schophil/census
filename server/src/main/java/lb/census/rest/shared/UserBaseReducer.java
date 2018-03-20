package lb.census.rest.shared;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public Optional<User> reduce(String userId) {
        return Optional.ofNullable(userBaseMap.remove(userId));
    }

    public Collection<User> remaining() {
        return userBaseMap.values();
    }
}
