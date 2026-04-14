package sg.edu.nus.laps.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import sg.edu.nus.laps.auth.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // READ
    // ID = email, but coding queries by email for readability/reusability

    // CHeck if user exists by email
    boolean existsByEmail(String email);

    // Find all users by email
    public Optional<User> findByEmail(String email);

    // Find user by email and enabled
    public Optional<User> findByEmailAndEnabledTrue(String email);

    // Find list of active users ordered by latest update
    List<User> findByEnabledOrderByUpdatedAtDesc(boolean enabled);

    // Find list of users by role ordered by latest update
    List<User> findByRole_IdOrderByUpdatedAtDesc(Long roleId);
    
    @Query("select count(u) from User u where u.role.id = :roleId")
    Integer countByRoleId(String roleId);
    

}
