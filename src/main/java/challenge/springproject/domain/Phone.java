package challenge.springproject.domain;

import javax.persistence.*;

@Entity
public class Phone {

    @Id
    @GeneratedValue
    private Long id;

    private String number;

    private String ddd;

    public Phone(String number, String ddd) {
        this.number = number;
        this.ddd = ddd;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

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

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }
}
