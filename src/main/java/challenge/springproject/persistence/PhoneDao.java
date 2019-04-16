package challenge.springproject.persistence;

import challenge.springproject.domain.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneDao extends JpaRepository<Phone, Long> { }
