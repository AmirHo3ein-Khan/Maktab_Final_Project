package ir.maktabsharif.online_exam.controller;


import ir.maktabsharif.online_exam.config.CustomUserDetailsService;
import ir.maktabsharif.online_exam.config.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
private final JwtTokenProvider jwtTokenProvider;
private final CustomUserDetailsService customUserDetailsService;

    public HomeController(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }


    @GetMapping("/")
    public String home(){
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(){
        return "/login";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            UserDetails user = customUserDetailsService.loadUserByUsername(username);

            String token = jwtTokenProvider.generateToken(user);

            return ResponseEntity.ok(Map.of("token", token));

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
    }


    @GetMapping("/logout")
    public String logout(){
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
