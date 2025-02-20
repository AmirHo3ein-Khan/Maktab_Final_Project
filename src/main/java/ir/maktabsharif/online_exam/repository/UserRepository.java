package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.model.User;
import ir.maktabsharif.online_exam.model.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> , JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUserType(UserType userType);
}
