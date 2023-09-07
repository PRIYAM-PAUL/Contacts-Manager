package com.springboot.manager.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="USER")
public class User {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int uid;
	@NotBlank(message = "Username Should not be blank")
	@Size(min=2,max=18,message = "Username should be in betwwen 2 - 18 character !!")
	private String name;
	@Column(unique = true)
	@Email(message="Please check EMail !!" )
	private String email;
	@NotBlank(message = "password Should not be blank")
	@Size(message = "Password should be in betwwen 2 - 18 character !!")
	private String password;
	private String imageUrl;
	@Column(length = 500)
	private String description;
	private boolean enable;
	private String role;
	@OneToMany(mappedBy = "user" ,cascade = CascadeType.ALL,orphanRemoval = true)
	private List<Contact> contact= new ArrayList<>();
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public List<Contact> getContact() {
		return contact;
	}
	public void setContact(List<Contact> contact) {
		this.contact = contact;
	}
	@Override
	public String toString() {
		return "User [uid=" + uid + ", name=" + name + ", email=" + email + ", password=" + password + ", imageUrl="
				+ imageUrl + ", description=" + description + ", enable=" + enable + ", role=" + role  + "]";
	}
}
