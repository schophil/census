package lb.census.dao;

import java.util.List;

import lb.census.model.DayStats;
import lb.census.model.Resource;

/**
 * Created by philippe on 24/04/16.
 */
public interface ResourceDao {

    void save(Resource resource);

    /**
     * Returns the most used resources regardless of the subject or date. This only returns the resouces
     * for which the user id is empty.
     * @param top
     * @parma name
     * @return
     */
    List<Resource> getPopular(int top, String name);

    /**
     * Returns the most used resources for a given day stats (hence date + subject). This only returns the resouces
     * for which the user id is empty.
     * @param dayStats
     * @param top
     * @param name
     * @return
     */
    List<Resource> getPopular(DayStats dayStats, int top, String name);

    /**
     * Returns the most used resources for a given day stats (hence date + subject). This only returns the resouces
     * for which the user id is empty.
     * @param dayStats
     * @param top
     * @param name
     * @return
     */
    List<Resource> getPopular(DayStats dayStats, int top, String name, String userId);
}
