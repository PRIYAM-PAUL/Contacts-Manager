package com.springboot.manager.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDate;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.springboot.manager.dao.ContactDao;
import com.springboot.manager.dao.ManagerDao;
import com.springboot.manager.dao.OrderDao;
import com.springboot.manager.entities.Contact;
import com.springboot.manager.entities.MyOrder;
import com.springboot.manager.entities.User;
import com.springboot.manager.message.Message;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private ManagerDao dao;
	@Autowired
	private OrderDao oDao;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private ContactDao cDao;
	private User user;


	@ModelAttribute
	public void commonAttribute(Model m, Principal principal) {
		user = dao.getUserbyUserName(principal.getName());
		user.setName(user.getName().toUpperCase());
		long contactNum = user.getContact().size();
		m.addAttribute("contactNum", contactNum);
		m.addAttribute("user", user);
	    long millis=System.currentTimeMillis();  
		Date date = new Date(millis);
		m.addAttribute("date_time",date );
	}

	@GetMapping("/")
	@ResponseBody
	public String home() {
		return "home";
	}

	@GetMapping("/index")
	public String dashboard(Model m, Principal principal) {
		m.addAttribute("title", "User Dashboard : " + user.getName());
		return "user/dashboard";
	}

	@GetMapping("/add_contact")
	public String addContact(Model m) {
		m.addAttribute("title", "Add Contact ");
		m.addAttribute("contact", new Contact());
		m.addAttribute("heading", "Add Contact");
		m.addAttribute("button", "Add Contact");
		m.addAttribute("updateForm", false);
		return "user/add_contact";
	}

	@PostMapping("/contactadded")
	public String contactAdded(@Valid @ModelAttribute("contact") Contact contact, BindingResult bindingResult, Model m,
			Principal principal, @RequestParam("imageUrl") MultipartFile file, Model model,
			@RequestParam("updateForm") boolean updateForm, @RequestParam("image") String image) {
		try {
			System.out.println(updateForm);
			System.out.println(contact);
			User ContactUser = this.dao.getUserbyUserName(principal.getName());
			contact.setUser(ContactUser);
			if (!updateForm) {
				m.addAttribute("heading", "Add Contact");
				m.addAttribute("button", "Add Contact");
			} else {
				m.addAttribute("heading", "Update Contact");
				m.addAttribute("button", "Update Contact");
			}
			if (file.isEmpty()) {
				System.out.println("Please Upload Image also");
				contact.setImageUrl("contact.png");
				model.addAttribute("message",
						new Message("Form Process Successfully : Contact Added!!", "alert-success"));
			} else {
				if (updateForm) {
					if (image.equals("contact.png") || image.equals(null)) {
					} else {
						File deletefile = new ClassPathResource("static/images" + File.separator + image).getFile();
						deletefile.delete();
					}
				}

				contact.setImageUrl(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				model.addAttribute("message",
						new Message("Form Process Successfully : Contact Added!!", "alert-success"));
				if (updateForm) {
					model.addAttribute("message",
							new Message("Form Process Successfully : Contact Updated!!", "alert-success"));
				}
			}
			user.getContact().add(contact);
			this.dao.save(user);
			return "user/add_contact";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("Error: Form Processing Request Fail", "alert-danger"));
			m.addAttribute("heading", "Update Contact");
			m.addAttribute("button", "Update Form");
			return "user/add_contact";
		}
	}

	@GetMapping("/view_contact/{page}")
	public String viewContact(@PathVariable("page") int page, Model m, Principal principal,
			RedirectAttributes attributes) {
		m.addAttribute("title", "View Contact");
		String UserName = principal.getName();
		User user = this.dao.getUserbyUserName(UserName);
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.cDao.findAllContactByUser(user.getUid(), pageable);
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		attributes.addFlashAttribute("currentPages", page);
		return "user/viewContact";
	}

	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") int cid, Principal principal, Model m) throws IOException {
		Optional<Contact> optionalContact = this.cDao.findById(cid);
		Contact contact = optionalContact.get();
		User user = this.dao.getUserbyUserName(principal.getName());
		if (user.getUid() == contact.getUser().getUid()) {
//		Optional	contact.setUser(null);
			if (contact.getImageUrl().equals("contact.png")) {
				this.cDao.delete(contact);
			} else {
				File file = new ClassPathResource("static/images" + File.separator + contact.getImageUrl()).getFile();
				file.delete();
				user.getContact().remove(contact);
				this.dao.save(user);
			}
			m.addAttribute("message", new Message("Contact Delete successfully!", "alert-success"));
		} else {
			m.addAttribute("message", new Message("Autentication unsuccessfully!", "alert-danger"));

		}
		return "redirect:/user/view_contact/0";
	}

	@GetMapping("/contact/{cid}")
	public String profileContact(@PathVariable("cid") int cid, Model m, Principal principal) {
		Optional<Contact> optional = this.cDao.findById(cid);
		User user = this.dao.getUserbyUserName(principal.getName());
		m.addAttribute("title", "Contact : " + optional.get().getName());
		if (user.getUid() == (optional.get().getUser().getUid()))
			m.addAttribute("contact", optional.get());
		return "user/profile_contact";
	}

	@GetMapping("/update/{cid}")
	public String updateContact(@PathVariable("cid") int cid, Model m, Principal principal) {
		Optional<Contact> optional = this.cDao.findById(cid);
		Contact contact = optional.get();
		User user = this.dao.getUserbyUserName(principal.getName());
		if (user.getUid() == (contact.getUser().getUid()))
			m.addAttribute("contact", contact);
		m.addAttribute("title", "Update Contact");
		m.addAttribute("heading", "Update Contact");
		m.addAttribute("button", "Update contact");
		m.addAttribute("updateForm", true);
		return "user/add_contact";
	}

	@GetMapping("/profile")
	public String profile(Model m) {
		m.addAttribute("title", "Profile : " + user.getName());
		m.addAttribute("user", user);
		return "user/profile";
	}

	@GetMapping("/profileUpdate")
	public String updateProfile(Model m,Principal principal ) {
		m.addAttribute("title", "Update Profile");
		m.addAttribute("button", "Update Profile");
		m.addAttribute("heading", "Update Profile");
		m.addAttribute("updateForm", true);
		m.addAttribute("user", user);
		m.addAttribute("contact", this.dao.getUserbyUserName(principal.getName()).getContact());
		return "signup";

	}

	@GetMapping("/dashboard")
	public String dashborad(Model m, Principal principal) {
		return "user/dashboard";
	}

	@GetMapping("/setting")
	public String setting() {
		return "user/setting";
	}

	@PostMapping("/changePassword")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, Model m) {
		User user = this.dao.getUserbyUserName(principal.getName());
		if (bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
			user.setPassword(bCryptPasswordEncoder.encode(newPassword));
			this.dao.save(user);
			m.addAttribute("message", new Message("Password changed successfully", "alert-success"));
		} else {
			m.addAttribute("message", new Message("Old Password Incorrect!", "alert-danger"));

		}

		return "user/setting";
	}

	@GetMapping("/deleteUser")
	public String deleteUser(Principal principal, Model m) throws IOException {
		System.out.println("delete");
		List<Contact> contact = user.getContact();
		for(Contact c: contact) {
			deleteContact(c.getCid(),principal,m);
			}
		user.setContact(null);
		User user = this.dao.getUserbyUserName(principal.getName());
		this.dao.delete(user);
		return "redirect:/logout";
	}
//payment gateway

	@PostMapping("/payment")
	@ResponseBody
	public String payment(@RequestBody Map<String,Object> data,Principal principal) throws Exception{
		System.out.println(data);
		RazorpayClient razorpayClient = new RazorpayClient("rzp_test_MEJlQkdvCK9cX6", "ZdxxCK9u2eEQQokq3dHbUQj6");
		int amount = Integer.parseInt(data.get("amount").toString());
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("amount", amount*100);
		int receipt =ThreadLocalRandom.current().nextInt(100000,999999+1);
		jsonObject.put("currency", "INR");
		jsonObject.put("receipt", "tnx_"+receipt);
		Order create = razorpayClient.orders.create(jsonObject);
		System.out.println(create);
		MyOrder myOrder = new MyOrder();
		float amt = Float.parseFloat(create.get("amount").toString())/100;
		myOrder.setAmount(amt);
		myOrder.setOrderId(create.get("id"));
		myOrder.setReceipt(create.get("receipt"));
		myOrder.setPaymentId(null);
		myOrder.setUser(this.dao.getUserbyUserName(principal.getName()));
		myOrder.setStatus("created");
		this.oDao.save(myOrder);
		return create.toString();
	}
	@PostMapping("/payment_updated")
	public ResponseEntity<?> paymentUpdate(@RequestBody Map<String,Object> data) {
		System.out.println(data);
		MyOrder order = this.oDao.findByOrderId(data.get("order_id").toString());
		order.setPaymentId(data.get("payment_id").toString());
		order.setStatus(data.get("status").toString());
		this.oDao.save(order);
		return ResponseEntity.ok(Map.of("msg","updated"));
	}

}
