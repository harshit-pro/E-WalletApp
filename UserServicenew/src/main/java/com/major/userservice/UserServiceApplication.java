package com.major.userservice;

import com.major.userservice.dao.UserRepository;
//import com.major.userservice.models.User;
import com.major.userservice.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class UserServiceApplication  implements CommandLineRunner {
    @Autowired
    UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//		String transactionUser = "txn-service";
//		String password = "12345";
//		userRepository.save(User.builder()
//				.username(transactionUser)
//                        .email("harshitesting07@gmail.com ")
//                        .IsVerified(true)
//				.password(new BCryptPasswordEncoder().encode(password))
//				.authorities("svc")
//				.build());
    }

}
