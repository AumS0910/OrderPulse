package org.orderpulse.orderpulsebackend.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.orderpulse.orderpulsebackend.entity.Role;
import org.orderpulse.orderpulsebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Iterable<User> findByRole(Role role);

    long countByRole(Role role);


    @Query("SELECT u from User u WHERE" +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', : firstName, '%')) OR " + "LOWER(u.lastName) LIKE LOWER(CONCAT('%', : lastName, '%'))")
    Iterable<User> findByNameContaining(@Param("firstName")String firstName, @Param("lastName")String lastName);

}
