package ir.maktabsharif.online_exam.service;


import ir.maktabsharif.online_exam.model.Course;
import ir.maktabsharif.online_exam.model.Master;
import ir.maktabsharif.online_exam.model.dto.MasterDto;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface MasterService {
    void masterRegister(MasterDto masterDto);
    boolean updateMaster(Long id , MasterDto masterDto);
    Master findById(Long id);
    List<Master> findAll();
    Master findByUsername(String username);
    List<Course> findMasterCourses(Long masterId);
    boolean checkPassword(Master master, String oldPassword);
    void changePassword(Master master , String newPassword);

}
