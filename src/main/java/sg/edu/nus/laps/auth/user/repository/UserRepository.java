package sg.edu.nus.laps.auth.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import sg.edu.nus.laps.auth.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // READ
    // ID = email, but coding queries by email for readability/reusability

    // CHeck if user exists by email
    // SELECT COUNT(*) > 0 FROM User u WHERE u.email = ?1
    boolean existsByEmail(String email);

    // Find all users by email
    // SELECT * FROM User u WHERE u.email = ?1
    public Optional<User> findByEmail(String email);

    // Find user by email and enabled
    // SELECT * FROM User u WHERE u.email = ?1 AND u.enabled = 1
    public Optional<User> findByEmailAndEnabledTrue(String email);

    // Find list of active users ordered by latest update
    // SELECT * FROM User u WHERE u.enabled = ?1 ORDER BY updated_at DESC
    List<User> findByEnabledOrderByUpdatedAtDesc(boolean enabled);

    // Find list of users by role ordered by latest update
    // SELECT * FROM User u WHERE u.role.id = ?1 ORDER BY updated_at DESC
    List<User> findByRole_IdOrderByUpdatedAtDesc(Long roleId);
    
    @Query("select count(u) from User u where u.role.id = :roleId")
    Integer countByRoleId(String roleId);
    

}
