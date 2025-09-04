package com.major.userservice.dao;

import com.major.userservice.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;


public interface UserRepository extends CrudRepository<User, Integer> { // passing the type of the entity
    // and the type of the primary key


    User findByUsername(String phone); // yaha par username is phone number
    // This method will be used to find the user by phone number
}
