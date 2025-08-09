package ir.maktabsharif.online_exam.controller;


import ir.maktabsharif.online_exam.config.UserDetailsImpl;
import ir.maktabsharif.online_exam.model.dto.LoginDto;
import ir.maktabsharif.online_exam.model.dto.LoginResponse;
import ir.maktabsharif.online_exam.model.dto.request.AuthenticationRequest;
import ir.maktabsharif.online_exam.model.dto.request.RefreshRequest;
import ir.maktabsharif.online_exam.model.dto.response.AuthenticationResponse;
import ir.maktabsharif.online_exam.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
        AuthenticationResponse login = userService.login(authenticationRequest);
        return new ResponseEntity<>(login, HttpStatus.OK);
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@RequestBody RefreshRequest req) {
        return ResponseEntity.ok(userService.refreshToken(req));
    }
}