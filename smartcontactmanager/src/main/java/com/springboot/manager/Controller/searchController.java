package com.springboot.manager.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.manager.dao.ContactDao;
import com.springboot.manager.dao.ManagerDao;
import com.springboot.manager.entities.Contact;
import com.springboot.manager.entities.User;

@RestController
public class searchController {

	@Autowired
	private ManagerDao dao;
	@Autowired
	private ContactDao cDao;
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal )
	{
		User user =this.dao.getUserbyUserName(principal.getName());
//		Optional<List<Contact>> contacts = this.cDao.findByNameContainingAndUser(query, user);
		Optional<Contact> contacts = this.cDao.findById(1153);
		System.out.println(contacts.get());
		return ResponseEntity.ok(contacts);
	}
	
}
