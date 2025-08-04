package ir.maktabsharif.online_exam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] allowedPathsWithOutAuthentication = {"/", "/login", "/student/save", "/master/save"};
    private static final String[] managerPathAllowed  = {"/manager/**", "/course/**"};
    private static final String[] masterPathAllowed = {"/master/**" , "/exam/**", "/question/**"};
    private static final String[] studentPathAllowed = {"/student/**"};


    private final JwtAuthorizationFilter jwtAuthorizationFilter;


    public SecurityConfig(JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(allowedPathsWithOutAuthentication).permitAll()
                        .requestMatchers(managerPathAllowed).hasRole("MANAGER")
                        .requestMatchers(masterPathAllowed).hasRole("MASTER")
                        .requestMatchers(studentPathAllowed).hasRole("STUDENT")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthorizationFilter , UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
