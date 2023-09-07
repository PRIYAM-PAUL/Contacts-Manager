package com.springboot.manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.springboot.manager.dao.ManagerDao;
import com.springboot.manager.entities.User;

public class UserDetailsServiceImpl implements UserDetailsService {

	public UserDetailsServiceImpl() {
	}

	@Autowired
	private ManagerDao dao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = dao.getUserbyUserName(username);
		if (user == null) {
			throw new UsernameNotFoundException("UserName not found");
		}

		UserDetailImpl detailImpl = new UserDetailImpl(user);
		return detailImpl;

	}

}
