package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.model.Option;
import ir.maktabsharif.online_exam.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OptionRepository extends JpaRepository<Option , Long> {
}
