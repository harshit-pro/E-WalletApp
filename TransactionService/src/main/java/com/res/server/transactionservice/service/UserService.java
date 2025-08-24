package com.res.server.transactionservice.service;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;




@Service
public class UserService implements UserDetailsService {

    private RestTemplate restTemplate = new RestTemplate();



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String transactionUser= "txn-service";
        String password="1234";
        // this is used to set the headers for the request because we are using basic authentication
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(transactionUser, password);

        HttpEntity<String> request = new HttpEntity<>(headers); // this is used to create the request with the headers
        // BECAUSE I AM USING BASIC AUTHENTICATION, I NEED TO SET THE HEADERS FOR THE REQUEST

        // i am calling user-microservice  to get the user details / to fetch user details
        // it calls the user-microservice's class UserService method loadUserByUsername
        //so somehow i need to call this api /user/username  in user-microservice
        // For service account
        if(username.equals(transactionUser)) {
            return User.builder()
                    .username(transactionUser)
                    .password(passwordEncoder().encode(password))
                    .authorities("svc")
                    .build();

        }
        // there 2 ways to do this:
        // 1. using RestTemplate
        // 2. using Feign Client
        // For now, I will use RestTemplate to call the user-microservice's api

        String url = "http://localhost:10000/user/username/" + username;
        System.out.println("Calling user service to get user details for username: " + username);

        // For interservice communication, we can use RestTemplate to call the user service
        // i get json response from the user service
        ResponseEntity<JSONObject> response=restTemplate.exchange
                (url, HttpMethod.GET,
                        request,
                        JSONObject.class);
        System.out.println("Response from user service: " + response.getBody());
        // here, I am using exchange method of RestTemplate to call the user-service
        // exchange method is used to exchange the request and response with the user service
        JSONObject responseBody = null; // ho sakta it might be null
        // i will check if the response is successful or not
        assert response!=null;
        if(response.getStatusCode().is2xxSuccessful()){
            responseBody=response.getBody();
        }else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        System.out.println("password: " + responseBody.get("password"));
        return User.builder().password((String) responseBody.get("password"))
                .username((String) responseBody.get("username"))
                .authorities((String) responseBody.get("authorities"))
                .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
