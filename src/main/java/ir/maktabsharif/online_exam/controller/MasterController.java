package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.Master;
import ir.maktabsharif.online_exam.model.dto.MasterDto;
import ir.maktabsharif.online_exam.service.MasterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/master")
public class MasterController {
    private final MasterService masterService;

    public MasterController(MasterService masterService) {
        this.masterService = masterService;
    }

    @GetMapping("/panel")
    public String home() {
        return "master/masterPanel";
    }


    @GetMapping("/save")
    public String showSaveMasterForm(Model model) {
        model.addAttribute("master", new MasterDto());
        return "master/register";
    }

    @PostMapping("/save")
    public String saveMaster(@ModelAttribute("user") MasterDto masterDto) {
        masterService.saveMaster(masterDto);
        return "redirect:/login?success";
    }

    @GetMapping("/edit/{id}")
    public String editMasterForm(@PathVariable Long id, Model model) {
        Master master = masterService.findById(id);
        model.addAttribute("master", master);
        return "master/edit-master";
    }

    @PostMapping("/edit/{id}")
    public String updateMaster(@PathVariable Long id, @ModelAttribute("user") MasterDto masterDto) {
        boolean isUpdate = masterService.updateMaster(id, masterDto);
        if (isUpdate) {
            return "redirect:/master/edit?success";
        }
        return "redirect:/master/edit/{id}";
    }
}
