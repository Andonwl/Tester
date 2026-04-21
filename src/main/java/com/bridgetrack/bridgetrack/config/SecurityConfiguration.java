package com.bridgetrack.bridgetrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.bridgetrack.bridgetrack.repository.UserRepository;
import com.bridgetrack.bridgetrack.repository.InstructorRepository;
import com.bridgetrack.bridgetrack.repository.StudentRepository;

@Configuration
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            StudentRepository studentRepo,
            InstructorRepository instructorRepo,
            UserRepository userRepo,
            PasswordEncoder passwordEncoder
    ) {
        return loginValue -> {

            if ("instructor22@bridgetrack.edu".equalsIgnoreCase(loginValue)) {
                return User.withUsername("instructor22@bridgetrack.edu")
                        .password(passwordEncoder.encode("instructor123"))
                        .roles("INSTRUCTOR")
                        .build();
            }

            if ("admin@bridgetrack.com".equalsIgnoreCase(loginValue)) {
                return User.withUsername("admin@bridgetrack.com")
                        .password(passwordEncoder.encode("admin123"))
                        .roles("ADMIN")
                        .build();
            }

            var student = studentRepo.findByStudentEmailIgnoreCase(loginValue);
            if (student.isPresent()) {
                var u = student.get();
                return User.withUsername(u.getStudentEmail())
                        .password(u.getPasswordHash())
                        .roles("STUDENT")
                        .build();
            }

            var instructor = instructorRepo.findByEmailIgnoreCase(loginValue);
            if (instructor.isPresent()) {
                var u = instructor.get();
                return User.withUsername(u.getEmail())
                        .password(u.getPassword())
                        .roles("INSTRUCTOR")
                        .build();
            }

            var user = userRepo.findByEmailIgnoreCase(loginValue);
            if (user.isPresent()) {
                var u = user.get();

                if ("ADMIN".equalsIgnoreCase(u.getEntityType())
                        && Boolean.TRUE.equals(u.getIsActive())) {
                    return User.withUsername(u.getEmail())
                            .password(u.getPasswordHash())
                            .roles("ADMIN")
                            .build();
                }
            }

            throw new UsernameNotFoundException("User not found: " + loginValue);
        };
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            var authorities = authentication.getAuthorities();

            boolean isAdmin = authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isInstructor = authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));
            boolean isStudent = authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

            var handler = new org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler();

            if (isAdmin) {
                handler.setDefaultTargetUrl("/admindashboard");
            } else if (isInstructor) {
                handler.setDefaultTargetUrl("/instructor/dashboard");
            } else if (isStudent) {
                handler.setDefaultTargetUrl("/student/course-planner");
            } else {
                handler.setDefaultTargetUrl("/loginl?error=true");
            }

            handler.setAlwaysUseDefaultTargetUrl(true);
            handler.onAuthenticationSuccess(request, response, authentication);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/homepage",
                    "/registration",
                    "/register",
                    "/login",
                    "/login.html",
                    "/catalog",
                    "/api/catalog/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/webjars/**",
                    "/bridgetrack.css",
                    "/bridgetrack.js",
                    "/favicon.ico"
                ).permitAll()

                .requestMatchers("/admindashboard").hasRole("ADMIN")
                .requestMatchers("/instructor/dashboard", "/instructor/rosters").hasAnyRole("INSTRUCTOR", "ADMIN")
                .requestMatchers("/student/dashboard").hasRole("STUDENT")

                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(authenticationSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )

            .httpBasic(Customizer.withDefaults())

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll()
            );

        return http.build();
    }
}