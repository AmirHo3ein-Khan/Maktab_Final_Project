package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {
}
