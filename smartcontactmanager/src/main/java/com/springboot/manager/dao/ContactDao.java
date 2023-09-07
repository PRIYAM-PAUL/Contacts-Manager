package com.springboot.manager.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.manager.entities.Contact;
import com.springboot.manager.entities.User;

import jakarta.transaction.Transactional;

public interface ContactDao extends JpaRepository<Contact, Integer>{
	
	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> findAllContactByUser(@Param("userId") int userId,Pageable pageable);
	
	public Optional<List<Contact>> findByNameContainingAndUser(String name,User user);

}
