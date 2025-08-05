package ir.maktabsharif.online_exam.controller;


import ir.maktabsharif.online_exam.config.CustomUserDetailsService;
import ir.maktabsharif.online_exam.config.JwtTokenProvider;
import ir.maktabsharif.online_exam.config.UserDetailsImpl;
import ir.maktabsharif.online_exam.model.dto.LoginDto;
import ir.maktabsharif.online_exam.model.dto.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;

    public HomeController(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService, AuthenticationManager authenticationManager) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            var auth = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
            Authentication authenticate = authenticationManager.authenticate(auth);
            UserDetailsImpl userDetails = (UserDetailsImpl) authenticate.getPrincipal();


            String token = jwtTokenProvider.generateToken(userDetails);

            return ResponseEntity.ok(new LoginResponse(token));

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
    }


    @GetMapping("/logout")
    public String logout() {
        return "login";
    }

    @GetMapping("/exceptionPage")
    public String showErrorPage() {
        return "exception-message";
    }

    @GetMapping("/registerError")
    public String showRegisterErrorPage() {
        return "register-error";
    }


}
