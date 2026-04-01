package sg.edu.nus.laps.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sg.edu.nus.laps.auth.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}
