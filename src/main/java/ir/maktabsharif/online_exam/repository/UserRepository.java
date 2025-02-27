package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.model.Role;
import ir.maktabsharif.online_exam.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> , JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByRole(Role role);
    @Query("SELECT u FROM User u JOIN u.role r " +
            "WHERE r.name = :roleName AND " +
            "(u.firstName LIKE CONCAT('%', :name, '%') " +
            "OR u.lastName LIKE CONCAT('%', :name, '%') " +
            "OR u.username LIKE CONCAT('%', :name, '%'))")
    List<User> findUserByRoleAndName(@Param("roleName") String roleName, @Param("name") String name);
}
