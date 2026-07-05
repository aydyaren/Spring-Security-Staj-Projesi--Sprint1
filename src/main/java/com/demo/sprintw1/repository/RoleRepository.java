package com.demo.sprintw1.repository;

import com.demo.sprintw1.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
//<hangi entity için işlem yapıldığını gösterir, entity'nin primary key türünü gösterir>
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);


}
