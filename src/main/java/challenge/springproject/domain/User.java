package challenge.springproject.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String email;

    private String password;

    @OneToMany(mappedBy = "userId")
    private List<Phone> phones;

    private LocalDate created;

    @Column(name = "last_login")
    private LocalDate lastLogin;

    private String token;

    public User() { }

    public User(String name, String email, String password, List<Phone> phones, LocalDate created, LocalDate lastLogin, String token) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phones = phones;
        this.created = created;
        this.lastLogin = lastLogin;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public LocalDate getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDate lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
