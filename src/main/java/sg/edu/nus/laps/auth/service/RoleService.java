package sg.edu.nus.laps.auth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import sg.edu.nus.laps.auth.model.Role;
import sg.edu.nus.laps.auth.repository.RoleRepository;

/**
 * RoleService provides methods:
 * 
 * 1. Retrieve roles by name
 * 2. Check if a role exists by name
 * 3. Retrieve all roles using RoleRepository
 */
@Service
public class RoleService {
    private final RoleRepository rRepo;
    public RoleService(RoleRepository rRepo) { this.rRepo = rRepo; }
    
    public Optional<Role> findRoleByName(String name) { return rRepo.findByName(name); }
    public boolean roleExistsByName(String name) { return rRepo.existsByName(name); }
    public List<Role> findAllRoles() { return rRepo.findAll(); }
}
