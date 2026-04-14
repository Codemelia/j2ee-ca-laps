package sg.edu.nus.laps.auth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import sg.edu.nus.laps.auth.model.Role;
import sg.edu.nus.laps.auth.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository rRepo;
    public RoleService(RoleRepository rRepo) { this.rRepo = rRepo; }
    
    // Retrieve role by name
    public Optional<Role> findRoleByName(String name) { return rRepo.findByName(name); }

    // Check if role exists by name
    public boolean roleExistsByName(String name) { return rRepo.existsByName(name); }
    
    // Retrieve all roles
    public List<Role> findAllRoles() { return rRepo.findAll(); }

}
