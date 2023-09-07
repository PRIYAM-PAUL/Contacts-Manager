package com.springboot.manager.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.manager.entities.MyOrder;

public interface OrderDao extends JpaRepository<MyOrder, Long> {
	
	public MyOrder findByOrderId(String orderId);

}
