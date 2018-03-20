package lb.census.rest.subjects.stats;

import lb.census.model.User;
import lb.census.model.UserStats;
import lb.census.rest.shared.Metrics;

import java.util.Optional;

/**
 * Created by philippe on 30/12/15.
 */
public class OneUser extends Metrics {

    public String userId;
    public String userName;

    public static OneUser of(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        OneUser oneUser = new OneUser();
        oneUser.userId = user.getUserId();
        oneUser.userName = user.getName();
        return oneUser;
    }

    public static OneUser of(UserStats userStats, Optional<User> user) {
        if (userStats == null) {
            throw new IllegalArgumentException("User stats is null.");
        }

        OneUser oneUser = new OneUser();
        oneUser.userId = userStats.getUserId();
        oneUser.map(userStats);

        if (user.isPresent()) {
            oneUser.userName = user.get().getName();
        } else {
            oneUser.userName = "unknown";
        }

        return oneUser;
    }
}
