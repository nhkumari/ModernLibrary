package com.neha.repository;

import org.springframework.data.repository.CrudRepository;
import com.neha.model.User;


public interface UserRepository extends CrudRepository<User, Long> {

}
