package challenge.springproject.business;

import challenge.springproject.domain.Phone;
import challenge.springproject.domain.User;
import challenge.springproject.dto.RegisterDto;
import challenge.springproject.exceptions.UserNotFoundException;
import challenge.springproject.persistence.UserCrudDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserCrudDao dao;

    @Autowired
    public UserService(UserCrudDao dao) {
        this.dao = dao;
    }

    public User create(RegisterDto dto) {
        User newUser = new User();
        newUser.setName(dto.getName());
        newUser.setPassword(dto.getPassword());
        newUser.setPhones(dto.getPhones().stream().map(phone -> new Phone(phone.getNumber(), phone.getDdd())).collect(Collectors.toList()));
        newUser.setCreated(LocalDate.now());
        newUser.setLastLogin(LocalDate.now());
        newUser.setToken(null);
        dao.save(newUser);
        return newUser;
    }


    public User getOne(Long id) {
        return dao.getById(id);
    }
}
