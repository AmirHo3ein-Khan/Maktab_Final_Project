package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.Master;
import ir.maktabsharif.online_exam.model.dto.ChangePasswordDto;
import ir.maktabsharif.online_exam.model.dto.MasterDto;
import ir.maktabsharif.online_exam.model.dto.response.ApiResponseDto;
import ir.maktabsharif.online_exam.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/master")
public class MasterController {
    private final MasterService masterService;

    public MasterController(MasterService masterService) {
        this.masterService = masterService;

    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto> registerMaster(@Valid @RequestBody MasterDto masterDto) {
        masterService.masterRegister(masterDto);
        String msg = "register.master.success";
        ApiResponseDto body = new ApiResponseDto(msg , true);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PreAuthorize("hasRole('MASTER')")
    @PutMapping("/edit/{id}")
    public ResponseEntity<ApiResponseDto> updateMaster(@PathVariable("id") Long id,@RequestBody @Valid MasterDto masterDto) {
        boolean isUpdate = masterService.updateMaster(id, masterDto);
        if (isUpdate) {
            String msg = "update.master.success";
            return ResponseEntity.ok(new ApiResponseDto(msg , true));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PreAuthorize("hasRole('MASTER')")
    @PutMapping("/change/pass")
    public ResponseEntity<ApiResponseDto> changePass(@RequestBody ChangePasswordDto  changePasswordDto ,Principal principal) {
        String username = principal.getName();
        Master master = masterService.findByUsername(username);
        if (master == null) {
            throw new RuntimeException("User not found!");
        }
        if (!masterService.checkPassword(master, changePasswordDto.getOldPassword())) {
            String msg = "check.password.incorrect";
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDto(msg , true));
        }
        masterService.changePassword(master, changePasswordDto.getNewPassword());
        String msg = "update.password.success";
        return ResponseEntity.ok(new ApiResponseDto(msg , true));
    }
}
