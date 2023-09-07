package com.springboot.manager.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="orders")
public class MyOrder{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long myOrderid;
	private String orderId;
	private String receipt;
	private float amount;
	private String status;
	@ManyToOne
	private User user;
	private String paymentId;
	
	public MyOrder() {
		super();
		// TODO Auto-generated constructor stub
	}
	public long getMyOrderid() {
		return myOrderid;
	}
	public void setMyOrderid(long myOrderid) {
		this.myOrderid = myOrderid;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getReceipt() {
		return receipt;
	}
	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	public MyOrder(long myOrderid, String orderId, String receipt, float amount, String status, User user,
			String paymentId) {
		super();
		this.myOrderid = myOrderid;
		this.orderId = orderId;
		this.receipt = receipt;
		this.amount = amount;
		this.status = status;
		this.user = user;
		this.paymentId = paymentId;
	}
	

}