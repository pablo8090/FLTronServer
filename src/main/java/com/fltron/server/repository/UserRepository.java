package com.fltron.server.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fltron.server.entities.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long>{
	List<User> findByUsername(String username);
	List<User> findByEmail(String email);
}
