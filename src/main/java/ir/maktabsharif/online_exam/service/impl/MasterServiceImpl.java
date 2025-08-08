package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.exception.EntityNotFoundException;
import ir.maktabsharif.online_exam.model.Course;
import ir.maktabsharif.online_exam.model.Master;
import ir.maktabsharif.online_exam.model.Role;
import ir.maktabsharif.online_exam.model.dto.MasterDto;
import ir.maktabsharif.online_exam.model.dto.response.CourseResponseDto;
import ir.maktabsharif.online_exam.model.enums.RegisterState;
import ir.maktabsharif.online_exam.repository.CourseRepository;
import ir.maktabsharif.online_exam.repository.MasterRepository;
import ir.maktabsharif.online_exam.repository.RoleRepository;
import ir.maktabsharif.online_exam.service.MasterService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class MasterServiceImpl implements MasterService {
    private final MasterRepository masterRepository;
    private final RoleRepository roleRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public MasterServiceImpl(MasterRepository masterRepository, RoleRepository roleRepository,
                             CourseRepository courseRepository, PasswordEncoder passwordEncoder) {
        this.masterRepository = masterRepository;
        this.roleRepository = roleRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void masterRegister(MasterDto masterDto) {
        Role masterRole = roleRepository.findByName("MASTER").get();
        masterRepository.save(Master.builder()
                .firstName(masterDto.getFirstName())
                .lastName(masterDto.getLastName())
                .username(masterDto.getUsername())
                .password(passwordEncoder.encode(masterDto.getPassword()))
                .email(masterDto.getEmail())
                .dob(masterDto.getDob())
                .role(masterRole)
                .registerState(RegisterState.WAITING)
                .build());
    }



    @Override
    public boolean updateMaster(Long id, MasterDto masterDto) {
        Optional<Master> master = masterRepository.findById(id);
        if (master.isPresent()) {
            Master updatedMaster = master.get();
            updatedMaster.setFirstName(masterDto.getFirstName());
            updatedMaster.setLastName(masterDto.getLastName());
            updatedMaster.setUsername(masterDto.getUsername());
            updatedMaster.setEmail(masterDto.getEmail());
            updatedMaster.setDob(masterDto.getDob());
            masterRepository.save(updatedMaster);
            return true;
        }
        return false;
    }

    @Override
    public Master findById(Long id) {
        return masterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Master not found with this id:" + id));
    }


    @Override
    public List<Master> findAll() {
        return masterRepository.findAll();
    }

    @Override
    public Master findByUsername(String username) {
        return masterRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Master not found with this username:" + username));
    }

    @Override
    public List<CourseResponseDto> findMasterCourses(Long masterId) {
        Master master = findById(masterId);
        List<Course> masterCourses = courseRepository.findByMaster(master);
        List<CourseResponseDto> courseDtoList = new ArrayList<>();
        for (Course course: masterCourses){
            courseDtoList.add(CourseResponseDto.builder()
                    .title(course.getTitle())
                    .courseStartedDate(course.getCourseStartedDate())
                    .courseFinishedDate(course.getCourseFinishedDate())
                    .unit(course.getUnit())
                    .build());
        }
        return courseDtoList;
    }

    @Override
    public boolean checkPassword(Master master, String oldPassword) {
        return passwordEncoder.matches(oldPassword, master.getPassword());
    }

    @Override
    public void changePassword(Master master, String newPassword) {
        master.setPassword(passwordEncoder.encode(newPassword));
        masterRepository.save(master);
    }
}
