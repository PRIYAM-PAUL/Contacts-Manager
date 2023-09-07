package com.springboot.manager.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.manager.entities.User;


public interface ManagerDao extends JpaRepository<User, Integer> {
	
	@Query("select a from User a where a.email=:email")
	public User getUserbyUserName(@Param("email")String email);

}
