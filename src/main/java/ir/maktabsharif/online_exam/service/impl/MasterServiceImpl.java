package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.exception.ResourcesNotFundException;
import ir.maktabsharif.online_exam.model.Master;
import ir.maktabsharif.online_exam.model.dto.MasterDto;
import ir.maktabsharif.online_exam.model.enums.RegisterState;
import ir.maktabsharif.online_exam.model.enums.UserType;
import ir.maktabsharif.online_exam.repository.MasterRepository;
import ir.maktabsharif.online_exam.service.MasterService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MasterServiceImpl  implements MasterService {
    private final MasterRepository masterRepository;
    private final PasswordEncoder passwordEncoder;

    public MasterServiceImpl(MasterRepository masterRepository, PasswordEncoder passwordEncoder) {
        this.masterRepository = masterRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveMaster(MasterDto masterDto) {
        masterRepository.save( Master.builder()
                .firstName(masterDto.getFirstName())
                .lastName(masterDto.getLastName())
                .username(masterDto.getUsername())
                .password(passwordEncoder.encode(masterDto.getPassword()))
                .email(masterDto.getEmail())
                .dob(masterDto.getDob())
                .userType(UserType.MASTER)
                .registerState(RegisterState.WAITING)
                .build());
    }

    @Override
    public boolean updateMaster(Long id, MasterDto masterDto) {
        Optional<Master> master = masterRepository.findById(id);
        if (master.isPresent()){
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
                .orElseThrow(() -> new ResourcesNotFundException("Master not found!"));
    }


    @Override
    public List<Master> findAll() {
        return masterRepository.findAll();
    }

}
