package challenge.springproject.business;

import challenge.springproject.domain.User;
import challenge.springproject.exceptions.UserNotFoundException;
import challenge.springproject.persistence.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserDao dao;

    @Autowired
    public UserService(UserDao dao) {
        this.dao = dao;
    }

    public User create(User user) {
        return dao.save(user);
    }

    public List<User> getAll() {
        return dao.findAll();
    }

    public User getOne(Long id) throws UserNotFoundException {
        return dao.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}
