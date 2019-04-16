package challenge.springproject.domain;

import javax.persistence.*;

@Entity
public class Phone {

    @Id
    @GeneratedValue
    private Long id;

    private String number;

    private String ddd;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Phone(String number, String ddd, User user) {
        this.number = number;
        this.ddd = ddd;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDdd() {
        return ddd;
    }

    public void setDdd(String ddd) {
        this.ddd = ddd;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
