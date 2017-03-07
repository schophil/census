package lb.census.rest.categories;

import lb.census.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by philippeschottey on 06/03/2017.
 */
@RestController
@RequestMapping("/rest/categories")
public class CategoriesController {

    @Autowired
    private UserDao userDao;

    @RequestMapping
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(userDao.getCategories());
    }
}
