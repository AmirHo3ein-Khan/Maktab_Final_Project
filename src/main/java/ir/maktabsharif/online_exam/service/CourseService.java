package ir.maktabsharif.online_exam.service;

import ir.maktabsharif.online_exam.model.Course;
import ir.maktabsharif.online_exam.model.DescriptiveQuestion;
import ir.maktabsharif.online_exam.model.MultipleChoiceQuestion;
import ir.maktabsharif.online_exam.model.Student;
import ir.maktabsharif.online_exam.model.dto.*;
import ir.maktabsharif.online_exam.model.dto.questiondto.DeleteQuestionFromQuestionBankDto;
import ir.maktabsharif.online_exam.model.dto.response.CourseResponseDto;

import java.util.List;

public interface CourseService {
    void createCourse(CourseRequestDto courseRequestDto);
    void updateCourse(Long id , CourseRequestDto courseRequestDto);
    void updateMasterOfCourse(UpdateMasterOfCourseDto updateMasterOfCourseDto);
    void deleteCourse(Long id);
    List<CourseResponseDto> findAll();
    void addStudentToCourse(AddStudentToCourseDto addStudentToCourseDto);
    void deleteStudentFromCourse(DeleteStudentFromCourseDto deleteStudentFromCourseDto);
    void addMasterToCourse(AddMasterToCourseDto addMasterToCourseDto);
    Course findById(Long courseId);
    List<MultipleChoiceQuestion> mcqBank(Long courseId);
    List<DescriptiveQuestion> dqBank(Long courseId);
    void deleteQuestionFromQuestionBank(DeleteQuestionFromQuestionBankDto dto);

}
