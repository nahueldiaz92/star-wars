package com.conexa.starwars.repository;

import com.conexa.starwars.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
