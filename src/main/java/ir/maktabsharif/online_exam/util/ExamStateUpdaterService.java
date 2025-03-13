package ir.maktabsharif.online_exam.util;

import ir.maktabsharif.online_exam.model.Exam;
import ir.maktabsharif.online_exam.model.enums.ExamState;
import ir.maktabsharif.online_exam.repository.ExamRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExamStateUpdaterService {
    
    private final ExamRepository examRepository;

    public ExamStateUpdaterService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateExamStates() {
        LocalDate today = LocalDate.now();
        List<Exam> exams = examRepository.findAll();

        for (Exam exam : exams) {
            LocalDate examDate = exam.getExamDate();
            ExamState newState;

            if (examDate.isAfter(today)) {
                newState = ExamState.NOT_STARTED;
            } else if (examDate.isEqual(today)) {
                newState = ExamState.STARTED;
            } else {
                newState = ExamState.FINISHED;
            }

            if (!exam.getExamState().equals(newState)) {
                exam.setExamState(newState);
                examRepository.save(exam);
            }
        }
    }
}
