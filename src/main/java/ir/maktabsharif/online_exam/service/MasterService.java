package ir.maktabsharif.online_exam.service;


import ir.maktabsharif.online_exam.model.Master;
import ir.maktabsharif.online_exam.model.dto.AddCourseToMasterDto;
import ir.maktabsharif.online_exam.model.dto.MasterDto;

import java.util.List;

public interface MasterService {
    void saveMaster(MasterDto masterDto);
    boolean updateMaster(Long id , MasterDto masterDto);
    Master findById(Long id);
    List<Master> findAll();

}
