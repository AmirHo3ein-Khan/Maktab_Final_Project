package ir.maktabsharif.online_exam.config;

import ir.maktabsharif.online_exam.model.User;
import ir.maktabsharif.online_exam.model.enums.RegisterState;
import ir.maktabsharif.online_exam.service.UserService;
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

    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;

    public SecurityConfig(CustomUserDetailsService userDetailsService, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/student/save", "/master/save").permitAll()
                        .requestMatchers("/manager/**", "/course/**").hasRole("MANAGER")
                        .requestMatchers("/master/**" , "/exam/**").hasRole("MASTER")
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                            User user = userService.findByUsername(userDetails.getUsername());
                            if (authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_MANAGER"))) {
                                response.sendRedirect("/manager/panel");
                            } else if (authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_MASTER"))) {
                                if (user.getRegisterState().equals(RegisterState.CONFIRM)) {
                                    response.sendRedirect("/master/panel");
                                } else {
                                    response.sendRedirect("/registerError");
                                }
                            } else if (authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_STUDENT"))) {
                                if (user.getRegisterState().equals(RegisterState.CONFIRM)) {
                                    response.sendRedirect("/student/panel");
                                } else {
                                    response.sendRedirect("registerError");
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
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
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
