package sg.edu.nus.laps.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sg.edu.nus.laps.auth.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // READ

    // Find by email
    public Optional<User> findByEmail(String email);

    

}
