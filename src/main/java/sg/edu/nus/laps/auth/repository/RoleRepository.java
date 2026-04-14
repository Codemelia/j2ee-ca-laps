package sg.edu.nus.laps.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sg.edu.nus.laps.auth.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Find by Name
    public Optional<Role> findByName(String name);

    // Check if role exists
    public boolean existsByName(String name);

    // Get role id by name
    public String findIdByName(String name);

}
