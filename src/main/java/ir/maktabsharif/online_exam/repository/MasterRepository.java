package ir.maktabsharif.online_exam.repository;


import ir.maktabsharif.online_exam.model.Master;
import ir.maktabsharif.online_exam.model.Student;
import ir.maktabsharif.online_exam.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterRepository extends JpaRepository<Master, Long> {
    Optional<Master> findByUsername(String username);
}
