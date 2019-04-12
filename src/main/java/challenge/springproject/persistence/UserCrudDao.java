package challenge.springproject.persistence;

import challenge.springproject.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserCrudDao extends CrudDao<User> {
    public UserCrudDao(){
        super(User.class);
    }
}
