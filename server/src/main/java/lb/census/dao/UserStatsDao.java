package lb.census.dao;

import lb.census.model.UserStats;

public interface UserStatsDao {

    void save(UserStats userStats);

    void delete(UserStats userStats);
}
