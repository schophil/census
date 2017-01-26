package lb.census.api.userbase;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lb.census.model.User;

/**
 * 
 * @author phili
 */
public class UserInfo {

    public String userId;
    public String name;
    public String category;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    public Date beginDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    public Date endDate;
    
    public UserInfo() {
    }
    
    public UserInfo(User user) {
        userId = user.getUserId();
        name = user.getName();
        category = user.getCategory();
        beginDate = user.getDateBegin();
        endDate = user.getDateEnd();
    }
}
