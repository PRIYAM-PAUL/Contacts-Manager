package com.springboot.manager.Controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.manager.dao.ManagerDao;
import com.springboot.manager.entities.Contact;
import com.springboot.manager.entities.User;
import com.springboot.manager.message.Message;
import com.springboot.manager.service.Mail;

import jakarta.validation.Valid;

@Controller
public class ManagerController {

	@Autowired
	private ManagerDao dao;
	@Autowired
	private Mail mail;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/")
	public String home(Model m) {
		m.addAttribute("title", "Smart Contact Manager : Home");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model m) {
		m.addAttribute("title", "Smart Contact Manager : About");
		return "about";
	}

	@GetMapping("/signup")
	public String signUp(Model m) {
		m.addAttribute("title", "Smart Contact Manager : Sign Up");
		m.addAttribute("button", "Sign Up");
		m.addAttribute("heading", "Sign Up");
		m.addAttribute("updateForm", false);
		m.addAttribute("user", new User());
		return "signup";
	}

	@PostMapping("/signed")
	public String signed(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model m,
			@RequestParam("updateForm") boolean updateForm, @RequestParam("contact") Optional<List<Contact>> contact) {
		try {
			if (!agreement) {
				throw new Exception(" : Please select our terms and condition");
			}
			user.setRole("ROLE_USER");
			user.setEnable(true);
			System.out.println(agreement);
			System.out.println(user);
			String realPassword=user.getPassword();
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			user.setImageUrl("default.png");
			m.addAttribute("updateForm", false);
			if (updateForm) {
				m.addAttribute("title", "Update Profile");
				m.addAttribute("button", "Update Profile");
				m.addAttribute("heading", "Update Profile");
				if(contact.get()==null) {
					user.setContact(null);

				}else{
				user.setContact(contact.get());
				}
			} else {
				m.addAttribute("title", "Smart Contact Manager : Sign Up");
				m.addAttribute("button", "Sign Up");
				m.addAttribute("heading", "Sign Up");
				String body = ""+
						"<div class='text-center' style='padding:20px ;position:fixed ;width:50%;left:50%;top:0;'>"
						+"<h3>"
						+"                       Hi, "+user.getName()+"  ,<br>" 
						+ "<br>"
						+ "ThankYou for Sign Up to your Smart Contact Manager account.<br>" 
						+ "<br>"
						+ "Your Username is <b>"+user.getName()+"</b> and your Password is :"+realPassword+"<br>"
						+ "<br>" 
						+ "<br>" 
						+ "<br>" 
						+ "If this wasn't you, please reset your password to secure your account."
						+"</h3>"
						+"</div>";
						
				
				boolean sendEmail = mail.sendEmail(user.getEmail(), "Welcome to Smart Contact Manager", body);
				if(!sendEmail)
				{
					m.addAttribute("message",new Message(" Invalid Email Address", "alert-danger"));
					m.addAttribute("user", user);
					return "singup";
				}
			}
			System.out.println(bindingResult.getAllErrors());
			if (bindingResult.hasErrors()) {
				m.addAttribute("user", user);
				return "signup";
			}
			User result = this.dao.save(user);
			System.out.println("running");
			m.addAttribute("user", user);
			m.addAttribute("message", new Message(" Process complete successfully ", "alert-success"));
			if (updateForm) {
				m.addAttribute("message", new Message("Update Process complete successfully ", "alert-success"));

			}
			return "signup";

		} catch (Exception e) {
			m.addAttribute("user", user);
			e.printStackTrace();
			m.addAttribute("message", new Message(" Something Went Wrong " + e.getMessage(), "alert-danger"));
			return "signup";
		}
	}

	@GetMapping("/login")
	public String login(Model m) {
		m.addAttribute("title", "Smart Contact Manager : Login");
		return "login";
	}

	@GetMapping("/forget")
	public String forgetPassword() {
		return "forget_password";
	}

	@PostMapping("/otp")
	public String otp(@RequestParam("emailData") String email, Model m) throws Exception {
		List<User> user = this.dao.findAll();
		int otp = ThreadLocalRandom.current().nextInt(1000, 9999 + 1);
		System.out.println(otp);
		System.out.println(email);
		
		for (User u : user) {
			if (email.equals(u.getEmail())) {
				System.out.println(u.getEmail());

				String subject = "A verification OTP mail from Smart Contact Manager";
				String bodyText = ""+
						"<div class='text-center' style='padding:20px ;position:fixed ;width:50%;left:50%;top:0;'>"
						+"<h3>"
						+"                       Hi, "+u.getName()+"  ,<br>" 
						+ "<br>"
						+ "Someone tried to log in to your Smart Contact Manager account.<br>" 
						+ "<br>"
						+ "If this was you, please use the following code to confirm your identity:<br>"
						+ "<br>" 
						+"<b>"
						+ otp 
						+"</b> "
						+ "<br>" 
						+ "<br>" 
						+ "If this wasn't you, please reset your password to secure your account."
						+"</h3>"
						+"</div>";
						
				
				 boolean sendEmail = mail.sendEmail(email, subject, bodyText); 
				 if(sendEmail)
				  {} else
				  { throw new Exception("Error sending mail"); }
				 
				m.addAttribute("message", new Message("OTP Send Successfully!!", "alert-success"));
				m.addAttribute("email", email);
				m.addAttribute("otp", otp);
				return "otp";
			}
		} 
				m.addAttribute("message", new Message("Sorry No email Found!!", "alert-danger"));
				m.addAttribute("email", email);
				return "forget_password";

			

	}

	@PostMapping("/otpVerify")
	public String verify(@RequestParam("otp") int otp, @RequestParam("dataOtp") int dataOtp,@RequestParam("email") String email, Model m) {
		System.out.println(otp);
		System.out.println(dataOtp);
		if (dataOtp == otp) {
			m.addAttribute("email",email);
			return "change-password";
		}
		m.addAttribute("email", email);
		m.addAttribute("message", new Message("Invalid OTP please try again", "alert-danger"));
		return "otp";
	}
	@PostMapping("/changePassword")
	public String changePassword(@RequestParam("newPassword")String newPassword,@RequestParam("Vemail")String Vemail,Model m) {
		User user =this.dao.getUserbyUserName(Vemail);
		user.setPassword(bCryptPasswordEncoder.encode(newPassword));
		this.dao.save(user);
		m.addAttribute("message",new Message("Password changed successfully","alert-success"));		
		return "login";
	}
}
