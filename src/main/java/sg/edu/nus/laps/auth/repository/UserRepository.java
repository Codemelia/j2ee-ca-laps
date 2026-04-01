package sg.edu.nus.laps.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sg.edu.nus.laps.auth.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
