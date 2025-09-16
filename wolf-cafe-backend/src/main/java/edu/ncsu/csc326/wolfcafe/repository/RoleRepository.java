package edu.ncsu.csc326.wolfcafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc326.wolfcafe.entity.Role;

/**
 * Repository interface for Roles.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
