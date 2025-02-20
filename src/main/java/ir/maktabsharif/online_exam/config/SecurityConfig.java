package ir.maktabsharif.online_exam.config;

import ir.maktabsharif.online_exam.model.User;
import ir.maktabsharif.online_exam.model.enums.RegisterState;
import ir.maktabsharif.online_exam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/student/save", "/master/save").permitAll()
                        .requestMatchers("/manager/**" , "/course/**").hasRole("MANAGER")
//                        .requestMatchers("/course/**").hasAnyRole("MASTER", "STUDENT")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                            User user = userService.findByUsername(userDetails.getUsername()).get();
                            if (authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_MANAGER"))) {
                                response.sendRedirect("/manager/panel");
                            } else if (authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_MASTER"))) {
                                if (user.getRegisterState().equals(RegisterState.CONFIRM)) {
                                    response.sendRedirect("/master/panel");
                                } else {
                                    response.sendRedirect("/error");
                                }
                            } else if (authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_STUDENT"))) {
                                if (user.getRegisterState().equals(RegisterState.CONFIRM)) {
                                    response.sendRedirect("/student/panel");
                                } else {
                                    response.sendRedirect("/error");
                                }
                            } else {
                                response.sendRedirect("/");
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
