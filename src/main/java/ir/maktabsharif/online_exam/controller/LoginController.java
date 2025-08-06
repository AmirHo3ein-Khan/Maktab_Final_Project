package ir.maktabsharif.online_exam.controller;


import ir.maktabsharif.online_exam.config.JwtTokenProvider;
import ir.maktabsharif.online_exam.config.UserDetailsImpl;
import ir.maktabsharif.online_exam.model.dto.LoginDto;
import ir.maktabsharif.online_exam.model.dto.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public LoginController(JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        this.jwtTokenProvider = jwtTokenProvider;
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
}