package challenge.springproject.persistence;

import challenge.springproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

public interface UserDao extends JpaRepository<User, Long> {

    User findByEmail(String email);

    @Modifying
    @Query("update User u set u.token = ?1 where u.id = ?2")
    void setToken(String token, Long id);

    User findByToken(String token);

}
