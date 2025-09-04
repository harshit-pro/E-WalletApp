package com.major.userservice.config;

import com.major.userservice.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {
    /**
     * This is the configuration class for the security of the application
     * Authentication
     *
     */
    @Bean
    public UserDetailsService userDetailsService() { // This method returns the UserService bean
        // isse hoga ye ki jab bhi userDetailsService() call hoga to UserService ka bean return hoga
        // and we can use it to load the user by username
        // This is the implementation of UserDetailsService interface
        /*
 `          Imagine Spring Security is like a receptionist that wants to check user details during login.
            You tell the receptionist:
            “Whenever someone logs in, go to this specific file cabinet (UserService) and find user info by username.”

            </>hat’s what you’re doing by returning new UserService() in userDetailsService().*/
        System.out.println("Creating UserDetailsService bean");
        return new UserService();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("/user/create", "/user/verify-otp").permitAll()
                                .requestMatchers("/user/profile-info").hasAnyAuthority("usr","svc") // this is for the user to access their profile info
                                .requestMatchers("/user/username/**").hasAnyAuthority("svc","usr"))

                // Temporarily allow all user endpoints
                    // this is for the service to access user by username
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .build();
    }
    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() { // This method returns the DaoAuthenticationProvider bean
        // DaoAuthenticationProvider is used to authenticate the user using the UserDetailsService
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(passwordEncoder());
        System.out.println("Creating DaoAuthenticationProvider bean");
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        System.out.println("DaoAuthenticationProvider is set with UserDetailsService");
        return daoAuthenticationProvider;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


